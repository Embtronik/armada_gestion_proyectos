package com.proyectos.administracion_documentos.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.proyectos.administracion_documentos.component.DocumentoConsumer;

@Configuration
public class RabbitConfig {
    public static final String DOCUMENTOS_QUEUE = "documentos.queue";
    public static final String COLA_ELIMINACION = "documentos.eliminacion";

    @Bean
    public Queue documentosQueue() {
        return new Queue(DOCUMENTOS_QUEUE, true, false, false);
    }

    @Bean
    public MessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer listenerContainer(
            ConnectionFactory connectionFactory,
            DocumentoConsumer consumer,
            MessageConverter messageConverter) {

        // Adaptador que invoca el método 'onMessage' del consumer y convierte el payload a Long
        MessageListenerAdapter adapter = new MessageListenerAdapter(consumer, "onMessage");
        adapter.setMessageConverter(messageConverter);

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(documentosQueue());
        container.setConcurrentConsumers(1);
        container.setMessageListener(adapter);
        return container;
    }
}
