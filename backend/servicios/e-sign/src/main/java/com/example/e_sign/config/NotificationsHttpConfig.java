package com.example.e_sign.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;

@Configuration
public class NotificationsHttpConfig {

  @Bean(name = "notificationsWebClient")
  public WebClient notificationsWebClient(
      @Value("${notifications.base-url}") String baseUrl,
      @Value("${notifications.connect-timeout-ms:3000}") int connectTimeoutMs,
      @Value("${notifications.read-timeout-ms:5000}") int readTimeoutMs) {

    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
        .responseTimeout(Duration.ofMillis(readTimeoutMs));

    return WebClient.builder()
        .baseUrl(baseUrl) // p.ej. http://localhost:8092
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }
}
