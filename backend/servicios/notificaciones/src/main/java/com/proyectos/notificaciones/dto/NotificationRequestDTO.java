package com.proyectos.notificaciones.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationRequestDTO {
  @Email @NotBlank
  private String to;

  @Email(message = "El campo 'from' debe ser un email válido")
  private String from; // opcional; si no llega, usar default-from

  @NotBlank
  private String nombrePlantilla;

  @NotNull
  private Map<String,Object> metadato;

  // Auditoría contextual
  private String servicioOrigen;
  private String tipoEvento;
  private String correlacionId;
}