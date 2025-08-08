package com.proyectos.notificaciones.mapper;

import com.proyectos.notificaciones.dto.NotificationRequestDTO;
import com.proyectos.notificaciones.dto.NotificationResponseDTO;
import com.proyectos.notificaciones.entity.Notificacion;
import com.proyectos.notificaciones.entity.NotificacionEstado;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificacionMapper {

    private final ObjectMapper mapper;

    public Notificacion toEntity(NotificationRequestDTO dto, String defaultFrom) {
        String from = (dto.getFrom() == null || dto.getFrom().isBlank()) ? defaultFrom : dto.getFrom();
        return Notificacion.builder()
                .toEmail(dto.getTo())
                .fromEmail(from)
                .nombrePlantilla(dto.getNombrePlantilla())
                .metadatoJson(serialize(dto.getMetadato()))
                .estado(NotificacionEstado.REQUESTED)
                .intentos(0)
                .fechaCreacion(OffsetDateTime.now())
                .servicioOrigen(dto.getServicioOrigen())
                .tipoEvento(dto.getTipoEvento())
                .correlacionId(dto.getCorrelacionId())
                .build();
    }

    private String serialize(Map<String, Object> meta) {
        try {
            return mapper.writeValueAsString(meta);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando metadato", e);
        }
    }

    public NotificationResponseDTO toResponse(Notificacion n) {
        return NotificationResponseDTO.builder()
                .id(n.getId())
                .estado(n.getEstado().name())
                .correlacionId(n.getCorrelacionId())
                .build();
    }
}