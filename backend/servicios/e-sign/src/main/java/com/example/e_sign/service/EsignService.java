package com.example.e_sign.service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.e_sign.client.NotificationsClient;
import com.example.e_sign.dto.NotificacionRequest;
import com.example.e_sign.entity.CodeRequestStatus;
import com.example.e_sign.entity.EsignCode;
import com.example.e_sign.entity.EsignCodeRequest;
import com.example.e_sign.repository.EsignCodeRepository;
import com.example.e_sign.repository.EsignCodeRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EsignService {

  private final EsignCodeRepository codeRepo;
  private final EsignCodeRequestRepository reqRepo;
  private final NotificationsClient notificationsClient;
  private final PasswordEncoder passwordEncoder;

  @Value("${esign.ttl-minutes:0}")
  private int ttlMinutes;
  @Value("${esign.ttl-seconds:300}")
  private int ttlSecondsFallback;

  @Value("${app.name:RDM}")
  private String appName;

  @Value("${support.email:soporte@empresa.com}")
  private String supportEmail;

  private static final SecureRandom RNG = new SecureRandom();

  private String sixDigits() {
    return String.format("%06d", RNG.nextInt(1_000_000));
  }

  private int effectiveTtlSeconds() {
    return (ttlMinutes > 0 ? ttlMinutes * 60 : ttlSecondsFallback);
  }

  /** Genera el OTP (hash en BD), actualiza la solicitud y envía correo. */
  @Transactional
  public void processRequestAndNotify(EsignCodeRequest req) {
    OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
    OffsetDateTime expires = nowUtc.plusSeconds(effectiveTtlSeconds());

    try {
      // 1) Generar OTP y guardar HASH en BD (campo 'code')
      String plainCode = sixDigits();
      String codeHash = passwordEncoder.encode(plainCode);

      EsignCode ec = EsignCode.builder()
          .userId(req.getUserId())
          .email(req.getEmail())
          .documentId(req.getDocumentId())     // documentId es String en tu entity
          .code(codeHash)                      // HASH, no el código en claro
          .createdAt(nowUtc)
          .expiresAt(expires)
          .build();
      ec = codeRepo.save(ec);

      // 2) Actualizar la solicitud
      req.setStatus(CodeRequestStatus.GENERATED);
      req.setProcessedAt(nowUtc);
      req.setEsignCodeId(ec.getId());
      req.setCodeExpiresAt(expires);
      reqRepo.save(req);

      // 3) Llamar micro de notificaciones (contrato: nombrePlantilla + metadato)
      Map<String, Object> meta = new HashMap<>();
      meta.put("appName", appName);
      meta.put("userName", null);            // si tienes nombre real, colócalo
      meta.put("code", plainCode);           // OTP en claro SOLO para el correo
      meta.put("expiresAt", expires.toString());
      meta.put("documentId", req.getDocumentId()); // ya es String
      meta.put("supportEmail", supportEmail);

      NotificacionRequest notif = NotificacionRequest.builder()
          .to(req.getEmail())
          .from("notificaciones@tu-dominio.com")                // ajusta si tu micro lo valida
          .subject("Tu código para firmar tu documento – " + appName)
          .nombrePlantilla("esign_otp")                         // nombre del .ftl en notificaciones
          .metadato(meta)                                       // variables para el template
          .servicioOrigen("e-sign")
          .tipoEvento("ESIGN_CODE")
          .correlacionId(req.getId().toString())
          .build();

      notificationsClient.enviar(notif);

      // Si agregas SENT en tu enum, podrías marcarlo aquí
      // req.setStatus(CodeRequestStatus.SENT);
      // reqRepo.save(req);

    } catch (Exception ex) {
      req.setStatus(CodeRequestStatus.FAILED);
      req.setProcessedAt(nowUtc);
      req.setErrorMessage(truncate(ex.getMessage(), 500));
      reqRepo.save(req);
      throw ex;
    }
  }

  /** Verifica el OTP (plain) contra el HASH y lo consume (one-time use). */
  @Transactional
  public boolean verifyAndConsume(String userId, String codePlain, Optional<UUID> documentIdOpt) {
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

    // Asegúrate de que el repo use el mismo tipo que tu entity (String si documentId es String)
    var candidates = codeRepo.findActiveCodes(userId,
        documentIdOpt.orElse(null), // si tu repo espera UUID, cambia entity/DTO; si espera String, ajusta firma
        now);

    for (var c : candidates) {
      if (passwordEncoder.matches(codePlain, c.getCode())) {
        c.setUsedAt(now);
        codeRepo.save(c);

        reqRepo.findByEsignCodeId(c.getId()).ifPresent(r -> {
          r.setStatus(CodeRequestStatus.VERIFIED);
          r.setProcessedAt(now);
          reqRepo.save(r);
        });

        return true;
      }
    }
    return false;
  }

  private String truncate(String s, int max) {
    if (s == null) return null;
    return s.length() <= max ? s : s.substring(0, max);
  }
}
