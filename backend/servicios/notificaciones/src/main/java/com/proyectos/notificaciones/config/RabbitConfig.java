package com.proyectos.notificaciones.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

  @Value("${notificaciones.rabbitmq.exchange}")
  private String exchangeName;
  @Value("${notificaciones.rabbitmq.queue}")
  private String queueName;
  @Value("${notificaciones.rabbitmq.dlq}")
  private String dlqName;
  @Value("${notificaciones.rabbitmq.routing-key}")
  private String routingKey;

  @Bean
  public TopicExchange notificacionesExchange() {
    return new TopicExchange(exchangeName, true, false);
  }

  @Bean
  public Queue notificacionesQueue() {
    return QueueBuilder.durable(queueName)
        .withArgument("x-dead-letter-exchange", exchangeName + ".dlx")
        .withArgument("x-dead-letter-routing-key", routingKey + ".dlq")
        .build();
  }

  @Bean
  public TopicExchange deadLetterExchange() {
    return new TopicExchange(exchangeName + ".dlx", true, false);
  }

  @Bean
  public Queue deadLetterQueue() {
    return QueueBuilder.durable(dlqName).build();
  }

  @Bean
  public Binding bindingNotifications(Queue notificacionesQueue, TopicExchange notificacionesExchange) {
    return BindingBuilder.bind(notificacionesQueue).to(notificacionesExchange).with(routingKey);
  }

  @Bean
  public Binding bindingDlq(Queue deadLetterQueue, TopicExchange deadLetterExchange) {
    return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(routingKey + ".dlq");
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
    return new RabbitTemplate(cf);
  }
}