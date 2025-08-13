package com.example.e_sign.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "esign_code_request")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EsignCodeRequest {

  @Id @GeneratedValue
  private UUID id;

  @Column(nullable = false, length = 120)
  private String userId;

  @Column(nullable = false, length = 320)
  private String email;

  @Column(length = 120)
  private String documentId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private CodeRequestStatus status;

  private OffsetDateTime createdAt;
  private OffsetDateTime processedAt;

  private String errorMessage;

  // EsignCodeRequest.java
  @Column(name = "esign_code_id")
  private UUID esignCodeId;

  private OffsetDateTime codeExpiresAt;
}
