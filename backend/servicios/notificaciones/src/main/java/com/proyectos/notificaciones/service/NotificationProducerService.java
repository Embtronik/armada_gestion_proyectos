package com.proyectos.notificaciones.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.proyectos.notificaciones.dto.NotificationRequestDTO;
import com.proyectos.notificaciones.entity.Notificacion;
import com.proyectos.notificaciones.entity.NotificacionEstado;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class NotificationProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final com.proyectos.notificaciones.respository.NotificacionRepository repository;

    @Value("${notificaciones.rabbitmq.exchange}")
    private String exchange;
    @Value("${notificaciones.rabbitmq.routing-key}")
    private String routingKey;
    @Value("${notificaciones.correo.default-from}")
    private String defaultFrom;

    @Transactional
    public Long enqueue(NotificationRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El DTO no puede ser nulo");
        }

        try {
            Notificacion entity = buildEntity(dto);
            entity = repository.save(entity);
            sendToRabbitMQ(entity);
            return entity.getId();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error procesando JSON", e);
        }
    }

    private Notificacion buildEntity(NotificationRequestDTO dto) throws JsonProcessingException {
        return Notificacion.builder()
                .toEmail(dto.getTo())
                .fromEmail(getFromEmail(dto.getFrom()))
                .nombrePlantilla(dto.getNombrePlantilla())
                .metadatoJson(objectMapper.writeValueAsString(dto.getMetadato()))
                .estado(NotificacionEstado.REQUESTED)
                .intentos(0)
                .fechaCreacion(OffsetDateTime.now())
                .servicioOrigen(dto.getServicioOrigen())
                .tipoEvento(dto.getTipoEvento())
                .correlacionId(dto.getCorrelacionId())
                .build();
    }

    private String getFromEmail(String from) {
        return StringUtils.hasText(from) ? from : defaultFrom;
    }

    private void sendToRabbitMQ(Notificacion entity) throws JsonProcessingException {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("id", entity.getId());

        MessageProperties properties = new MessageProperties();
        properties.setContentType("application/json");
        if (entity.getCorrelacionId() != null) {
            properties.setHeader("X-Correlation-Id", entity.getCorrelacionId());
        }

        Message message = new Message(
                objectMapper.writeValueAsBytes(payload),
                properties
        );

        rabbitTemplate.send(exchange, routingKey, message);
    }
}