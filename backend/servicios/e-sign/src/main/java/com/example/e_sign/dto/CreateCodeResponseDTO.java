package com.example.e_sign.dto;

// CreateCodeResponseDTO.java
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CreateCodeResponseDTO {
  private UUID requestId;
  private String status;          // QUEUED | GENERATED (si lo procesas inline)
  private OffsetDateTime expiresAt; // puede venir null mientras está en cola
}
