package org.example.agroguardian_backend.mqtt;

import com.influxdb.client.InfluxDBClient;
import org.example.agroguardian_backend.rabbitmq.RabbitMQConsumer;
import org.example.agroguardian_backend.rabbitmq.RabbitMQProducer;
import org.example.agroguardian_backend.telemetry.TelemetryService;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class MqttService {

    private MessageChannel mqttOutboundChannel;
    private RabbitMQProducer rabbitMQProducer;
    public void setMqttOutboundChannel(MessageChannel mqttOutboundChannel) {
        this.mqttOutboundChannel = mqttOutboundChannel;
    }

    public void setRabbitMQProducer(RabbitMQProducer rabbitMQProducer) {
        this.rabbitMQProducer = rabbitMQProducer;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        String topic = Objects.requireNonNull(message.getHeaders().get("mqtt_receivedTopic")).toString();
        String payload = message.getPayload().toString();

        if (topic.matches("agro/devices/.*/telemetry")) {
            rabbitMQProducer.sendTelemetry(payload);
        }
    }



    public void sendCommand(String deviceName, String command) {
        String topic = "agro/devices/" + deviceName + "/commands";
        mqttOutboundChannel.send(MessageBuilder.withPayload(command)
                .setHeader(MqttHeaders.TOPIC, topic)
                .build());
    }
}