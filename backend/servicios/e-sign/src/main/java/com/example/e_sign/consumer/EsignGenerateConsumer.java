package com.example.e_sign.consumer;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_sign.client.NotificationsClient;
import com.example.e_sign.dto.GenerateCodeMessage;
import com.example.e_sign.dto.NotificacionRequest;
import com.example.e_sign.entity.CodeRequestStatus;
import com.example.e_sign.entity.EsignCode;
import com.example.e_sign.repository.EsignCodeRepository;
import com.example.e_sign.repository.EsignCodeRequestRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EsignGenerateConsumer {

  private final EsignCodeRepository codeRepo;
  private final EsignCodeRequestRepository reqRepo;
  private final NotificationsClient notificationsClient;
  private final PasswordEncoder encoder;

  @Value("${esign.ttl-seconds:300}")
  private long ttlSeconds;

  @Value("${app.name:RDM}")
  private String appName;

  @Value("${support.email:soporte@empresa.com}")
  private String supportEmail;

  @RabbitListener(queues = "${esign.queue-generate}")
  @Transactional
  public void onMessage(GenerateCodeMessage msg) {
    log.info("Recibida solicitud de código: {}", msg.getRequestId());

    var req = reqRepo.findById(msg.getRequestId())
        .orElseThrow(() -> new IllegalStateException("Request no existe: " + msg.getRequestId()));

    // 1) Generar y hashear (guardamos el HASH en 'code')
    String plainCode = generarCodigo();              // ej. "AB12-7A"
    String codeHash  = encoder.encode(plainCode);
    OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
    OffsetDateTime expires = nowUtc.plusSeconds(ttlSeconds);

    var code = EsignCode.builder()
        .code(codeHash)                              // almacenamos HASH, no el código en claro
        .email(msg.getEmail())
        .userId(msg.getUserId())
        .documentId(msg.getDocumentId())
        .createdAt(nowUtc)
        .expiresAt(expires)
        .build();
    code = codeRepo.save(code);

    // 2) Actualizar la solicitud
    req.setStatus(CodeRequestStatus.GENERATED);
    req.setProcessedAt(nowUtc);
    req.setCodeExpiresAt(expires);
    req.setEsignCodeId(code.getId());
    reqRepo.save(req);

    // 3) Llamar micro de notificaciones (contrato: nombrePlantilla + metadato)
    try {
      Map<String, Object> meta = new HashMap<>();
      meta.put("appName", appName);
      meta.put("userName", null); // si tienes el nombre real, colócalo
      meta.put("code", plainCode); // OTP en claro SOLO para el correo
      meta.put("expiresAt", expires.toString()); // puedes formatear a zona local si quieres
      meta.put("documentId", String.valueOf(msg.getDocumentId()));
      meta.put("supportEmail", supportEmail);

      var notif = NotificacionRequest.builder()
          .to(msg.getEmail())
          .from("notificaciones@tu-dominio.com") // si tu micro lo requiere
          .subject("Tu código para firmar tu documento – " + appName)

          .nombrePlantilla("esign_otp") // <--- nombre del .ftl en el micro
          .metadato(meta)               // <--- variables para el template

          .servicioOrigen("e-sign")
          .tipoEvento("ESIGN_CODE")
          .correlacionId(msg.getRequestId().toString())
          .build();

      notificationsClient.enviar(notif);

      // Si agregas SENT en tu enum, puedes marcarlo aquí
      // req.setStatus(CodeRequestStatus.SENT);
      // reqRepo.save(req);

      log.info("Código generado y notificado (requestId={}, codeId={}, expira={})",
          req.getId(), code.getId(), expires);

    } catch (Exception ex) {
      req.setStatus(CodeRequestStatus.FAILED);
      req.setErrorMessage("Error notificando: " + abreviar(ex.getMessage(), 500));
      reqRepo.save(req);
      log.error("Error enviando notificación (requestId={}): {}", req.getId(), ex.getMessage(), ex);
      throw ex;
    }
  }

  private String generarCodigo() {
    // return String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000)); // numérico
    return UUID.randomUUID().toString().substring(0, 6).toUpperCase(); // alfanumérico corto
  }

  private String abreviar(String s, int max) {
    if (s == null) return null;
    return s.length() <= max ? s : s.substring(0, max);
  }
}
