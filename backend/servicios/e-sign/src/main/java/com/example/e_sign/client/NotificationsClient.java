package com.example.e_sign.client;

import com.example.e_sign.dto.NotificacionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class NotificationsClient {

  @Qualifier("notificationsWebClient")
  private final WebClient webClient;

  @Value("${notifications.path:/api/notificaciones}")
  private String path;

  /**
   * Envía el request al micro de notificaciones y espera respuesta 2xx.
   * Lanza WebClientResponseException si el micro responde != 2xx.
   */
  public void enviar(NotificacionRequest req) {
    webClient.post()
        .uri(path)              // p.ej. /api/notificaciones
        .bodyValue(req)
        .retrieve()
        .toBodilessEntity()
        .block();               // si prefieres no bloquear, retorna Mono<Void>
  }
}
