package com.example.e_sign.dto;

// VerifyCodeResponseDTO.java
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class VerifyCodeResponseDTO {
  private boolean ok;
  private String status; // OK | INVALID | EXPIRED | USED
  private OffsetDateTime verifiedAt;
}
