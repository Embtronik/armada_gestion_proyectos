package com.example.e_sign.dto;

// VerifyCodeRequestDTO.java
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyCodeRequestDTO {
  @NotBlank
  private String code;
  private String documentId; // opcional para validar asociación
}
