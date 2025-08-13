package com.example.e_sign.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificacionRequest {
  private String to;                 // correo destino
  private String from;               // opcional si tu micro lo valida
  private String subject;            // asunto

  // Estos son los que tu micro EXIGE:
  private String nombrePlantilla;    // <-- antes usabas 'template'
  private Map<String, Object> metadato; // <-- antes 'variables'

  // Campos extra opcionales:
  private String servicioOrigen;
  private String tipoEvento;
  private String correlacionId;
}
