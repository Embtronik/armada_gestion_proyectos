package com.example.e_sign.dto;

// GenerateCodeMessage.java (payload de la cola)
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class GenerateCodeMessage {
  private UUID requestId;
  private String userId;
  private String email;
  private String documentId;
}
