package com.example.e_sign.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

  @Value("${esign.exchange}")        String esignExchange;
  @Value("${esign.routing-generate}") String esignRoutingGenerate;
  @Value("${esign.queue-generate}")   String esignQueueGenerate;

  @Bean
  public TopicExchange esignTopicExchange() {
    return ExchangeBuilder.topicExchange(esignExchange).durable(true).build();
  }

  @Bean
  public Queue esignGenerateQueue() {
    return QueueBuilder.durable(esignQueueGenerate).build();
  }

  @Bean
  public Binding esignGenerateBinding() {
    return BindingBuilder.bind(esignGenerateQueue())
        .to(esignTopicExchange()).with(esignRoutingGenerate);
  }

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
