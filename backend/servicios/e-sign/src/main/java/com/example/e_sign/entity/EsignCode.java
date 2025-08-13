package com.example.e_sign.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "esign_code",
  indexes = @Index(name = "idx_esign_user_active", columnList = "userId,expiresAt,usedAt"))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EsignCode {

  @Id @GeneratedValue
  private UUID id;

  @Column(nullable = false, length = 120)
  private String userId;

  @Column(nullable = false, length = 320)
  private String email;

  @Column(length = 120)
  private String documentId;

  @Column(nullable = false, length = 100)
  private String code;

  @Column(nullable = false)
  private OffsetDateTime createdAt;

  @Column(nullable = false)
  private OffsetDateTime expiresAt;

  private OffsetDateTime usedAt;
}
