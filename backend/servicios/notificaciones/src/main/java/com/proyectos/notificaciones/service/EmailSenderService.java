package com.proyectos.notificaciones.service;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

  private final JavaMailSender mailSender;

  // Tomar remitente por defecto de tu YML (o cae al username del SMTP)
  @Value("${notificaciones.correo.default-from:${spring.mail.username}}")
  private String defaultFrom;

  // Nombre visible opcional
  @Value("${notificaciones.correo.from-name:Notificaciones}")
  private String fromName;

  // Lista de froms explícitos permitidos (opcional). Vacío = ninguno permitido.
  @Value("${notificaciones.correo.allowed-froms:}")
  private String allowedFroms;

  // (opcional) Reply-To si quieres que respondan a otra casilla
  @Value("${notificaciones.correo.reply-to:}")
  private String replyTo;

  /** API recomendada: el micro decide el FROM (ignora el que venga de afuera). */
  public void sendHtml(String to, String subject, String htmlBody) {
    sendHtmlInternal(null, to, subject, htmlBody);
  }

  /** Mantén la firma antigua, pero se validará contra whitelist. */
  public void sendHtml(@Nullable String requestedFrom, String to, String subject, String htmlBody) {
    sendHtmlInternal(requestedFrom, to, subject, htmlBody);
  }

  private void sendHtmlInternal(@Nullable String requestedFrom, String to, String subject, String htmlBody) {
    try {
      String from = resolveFrom(requestedFrom);

      MimeMessage mime = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");

      helper.setFrom(new InternetAddress(from, fromName));
      helper.setTo(to);
      if (replyTo != null && !replyTo.isBlank()) {
        helper.setReplyTo(replyTo);
      }
      helper.setSubject(subject);
      helper.setText(htmlBody, true);

      mailSender.send(mime);
    } catch (MessagingException | UnsupportedEncodingException ex) {
      throw new MailSendException("Error enviando email", ex);
    }
  }

  private String resolveFrom(@Nullable String requestedFrom) {
    Set<String> allow = Arrays.stream(allowedFroms.split(","))
        .map(String::trim).filter(s -> !s.isEmpty())
        .collect(Collectors.toSet());

    if (requestedFrom != null && !requestedFrom.isBlank() && allow.contains(requestedFrom)) {
      return requestedFrom;
    }
    return defaultFrom; // siempre caemos al remitente del micro
  }
}
