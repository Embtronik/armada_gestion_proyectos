package com.example.e_sign.dto;

// RequestStatusResponseDTO.java
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class RequestStatusResponseDTO {
  private UUID requestId;
  private String status;
  private UUID codeId;
  private OffsetDateTime expiresAt;
  private String errorMessage;
}
