package com.proyectos.notificaciones.entity;

import com.proyectos.notificaciones.entity.NotificacionEstado;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.Map;

// Notificacion.java
@Entity
@Table(name = "notificacion")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Notificacion {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="to_email", nullable=false, length=320)
  private String toEmail;

  @Column(name="from_email", nullable=false, length=320)
  private String fromEmail;

  @Column(name="nombre_plantilla", nullable=false, length=120)
  private String nombrePlantilla;

  // ✅ Guardamos JSON en texto
  @Lob // opcional; si prefieres usa columnDefinition = "text"
  @Column(name = "metadato_json", nullable = false, columnDefinition = "text")
  private String metadatoJson;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false, length=30)
  private NotificacionEstado estado;

  @Column(nullable=false)
  private Integer intentos;

  @Column(name="fecha_creacion", nullable=false)
  private OffsetDateTime fechaCreacion;

  @Column(name="fecha_envio")
  private OffsetDateTime fechaEnvio;

  @Column(name="servicio_origen", length=120)
  private String servicioOrigen;

  @Column(name="tipo_evento", length=120)
  private String tipoEvento;

  @Column(name="correlacion_id", length=120)
  private String correlacionId;

  @Column(name="error_ultimo")
  private String errorUltimo;
}

