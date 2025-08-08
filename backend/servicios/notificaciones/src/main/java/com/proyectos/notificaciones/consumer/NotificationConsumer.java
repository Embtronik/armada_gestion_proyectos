package com.proyectos.notificaciones.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectos.notificaciones.entity.Notificacion;
import com.proyectos.notificaciones.entity.NotificacionEstado;
import com.proyectos.notificaciones.service.EmailSenderService;
import com.proyectos.notificaciones.service.TemplateRenderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;

import com.proyectos.notificaciones.respository.NotificacionRepository;

@Slf4j
@Component @RequiredArgsConstructor
public class NotificationConsumer {

  private final ObjectMapper objectMapper;
  private final NotificacionRepository repository;
  private final TemplateRenderService templateRenderService;
  private final EmailSenderService emailSenderService;

  @Value("${notificaciones.rabbitmq.max-retry}")
  private int maxRetry;

  @RabbitListener(queues = "${notificaciones.rabbitmq.queue}")
  @Transactional
  public void onMessage(String raw) {
    try {
      JsonNode node = objectMapper.readTree(raw);     // espera {"id":123}
      long id = node.get("id").asLong();

      Notificacion n = repository.findById(id).orElseThrow();
      Map<String,Object> meta = parseMeta(n.getMetadatoJson()); // <- parsear el String JSON

      try {
        String subject = String.valueOf(meta.getOrDefault("subject", n.getNombrePlantilla()));
        String html = templateRenderService.render(n.getNombrePlantilla(), meta);
        emailSenderService.sendHtml(n.getFromEmail(), n.getToEmail(), subject, html);

        n.setEstado(NotificacionEstado.SENT);
        n.setFechaEnvio(OffsetDateTime.now());
        n.setErrorUltimo(null);
        repository.save(n);
        log.info("Notificación {} enviada a {}", n.getId(), n.getToEmail());

      } catch (Exception sendEx) {
        n.setIntentos(n.getIntentos() + 1);
        n.setErrorUltimo(sendEx.getMessage());
        if (n.getIntentos() >= maxRetry) {
          n.setEstado(NotificacionEstado.FAILED);
        }
        repository.save(n);
        log.error("Error enviando notificación {}: {}", n.getId(), sendEx.getMessage(), sendEx);
        throw sendEx; // para reintentos/DLQ
      }

    } catch (Exception e) {
      log.error("Error procesando mensaje: {}", raw, e);
      throw new RuntimeException(e);
    }
  }

  private Map<String,Object> parseMeta(String json) {
    try {
      return objectMapper.readValue(json, new TypeReference<Map<String,Object>>(){});
    } catch (Exception e) {
      throw new RuntimeException("Error parseando metadato_json", e);
    }
  }
}
