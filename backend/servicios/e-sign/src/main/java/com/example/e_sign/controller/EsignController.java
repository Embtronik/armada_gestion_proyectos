package com.example.e_sign.controller;

// EsignController.java
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.e_sign.dto.CreateCodeRequestDTO;
import com.example.e_sign.dto.CreateCodeResponseDTO;
import com.example.e_sign.dto.GenerateCodeMessage;
import com.example.e_sign.dto.RequestStatusResponseDTO;
import com.example.e_sign.dto.VerifyCodeRequestDTO;
import com.example.e_sign.dto.VerifyCodeResponseDTO;
import com.example.e_sign.entity.CodeRequestStatus;
import com.example.e_sign.entity.EsignCodeRequest;
import com.example.e_sign.repository.EsignCodeRequestRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/esign")
@RequiredArgsConstructor
public class EsignController {

  private final RabbitTemplate rabbit;
  private final EsignCodeRequestRepository reqRepo;
  private final com.example.e_sign.service.EsignService service;

  @Value("${esign.exchange}") String exchange;
  @Value("${esign.routing-generate}") String routing;

  @PostMapping("/requests")
  public ResponseEntity<CreateCodeResponseDTO> solicitar(@AuthenticationPrincipal Jwt jwt,
                                                         @Valid @RequestBody CreateCodeRequestDTO dto) {
    String userId = jwt.getSubject();
    String email  = (String) (jwt.getClaims().getOrDefault("email",
                     jwt.getClaims().getOrDefault("preferred_username", "no-email")));

    var req = EsignCodeRequest.builder()
        .userId(userId)
        .email(email)
        .documentId(dto.getDocumentId())
        .status(CodeRequestStatus.QUEUED)
        .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
        .build();
    req = reqRepo.save(req);

    var msg = GenerateCodeMessage.builder()
        .requestId(req.getId())
        .userId(userId)
        .email(email)
        .documentId(dto.getDocumentId())
        .build();

    rabbit.convertAndSend(exchange, routing, msg);

    return ResponseEntity.ok(
        CreateCodeResponseDTO.builder()
          .requestId(req.getId())
          .status(req.getStatus().name())
          .expiresAt(null) // se llenará cuando el consumer genere el código
          .build());
  }

  @GetMapping("/requests/{id}")
  public ResponseEntity<RequestStatusResponseDTO> estado(@PathVariable UUID id) {
    var req = reqRepo.findById(id).orElse(null);
    if (req == null) return ResponseEntity.notFound().build();

    return ResponseEntity.ok(
        RequestStatusResponseDTO.builder()
          .requestId(req.getId())
          .status(req.getStatus().name())
          .codeId(req.getEsignCodeId())
          .expiresAt(req.getCodeExpiresAt())
          .errorMessage(req.getErrorMessage())
          .build());
  }

  @PostMapping("/verify")
  public ResponseEntity<VerifyCodeResponseDTO> verificar(@AuthenticationPrincipal Jwt jwt,
                                                       @Valid @RequestBody VerifyCodeRequestDTO dto) {
    // Convertir String -> UUID (si viene)
    Optional<UUID> docIdOpt;
    try {
      docIdOpt = Optional.ofNullable(dto.getDocumentId())
                       .map(UUID::fromString);  // <-- aquí se convierte
    } catch (IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(
      VerifyCodeResponseDTO.builder()
        .ok(false)
        .status("INVALID_DOCUMENT_ID")
        .build()
    );
  }

    boolean ok = service.verifyAndConsume(jwt.getSubject(), dto.getCode(), docIdOpt);
    if (ok) {
      return ResponseEntity.ok(
      VerifyCodeResponseDTO.builder()
        .ok(true)
        .status("OK")
        .verifiedAt(OffsetDateTime.now(ZoneOffset.UTC))
        .build()
    );
  }
  return ResponseEntity.badRequest().body(
    VerifyCodeResponseDTO.builder().ok(false).status("INVALID_OR_EXPIRED").build()
  );
  }
}
