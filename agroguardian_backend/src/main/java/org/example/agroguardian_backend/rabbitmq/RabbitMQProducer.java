package org.example.agroguardian_backend.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendTelemetry(String message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_TELEMETRY,
                RabbitMQConfig.ROUTING_KEY_TELEMETRY,
                message
        );
    }
} 