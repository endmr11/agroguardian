package org.example.agroguardian_backend.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.agroguardian_backend.telemetry.TelemetryService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final TelemetryService telemetryService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_TELEMETRY)
    public void consumeTelemetry(String message) {
        telemetryService.saveTelemetryToInfluxDB(message);
    }

} 