package com.proyectos.notificaciones.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationResponseDTO {
  private Long id;
  private String estado;        // REQUESTED/SENT/FAILED
  private String correlacionId; // para tracking
}
