package org.example.agroguardian_backend.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_TELEMETRY = "telemetry.queue";
    public static final String EXCHANGE_TELEMETRY = "telemetry.exchange";
    public static final String ROUTING_KEY_TELEMETRY = "telemetry.routingkey";

    @Bean
    public Queue telemetryQueue() {
        return new Queue(QUEUE_TELEMETRY);
    }


    @Bean
    public DirectExchange telemetryExchange() {
        return new DirectExchange(EXCHANGE_TELEMETRY);
    }

    @Bean
    public Binding telemetryBinding(Queue telemetryQueue, DirectExchange telemetryExchange) {
        return BindingBuilder
                .bind(telemetryQueue)
                .to(telemetryExchange)
                .with(ROUTING_KEY_TELEMETRY);
    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
} 