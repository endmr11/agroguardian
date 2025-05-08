package org.example.agroguardian_backend.telemetry;

import com.influxdb.client.InfluxDBClient;
import org.example.agroguardian_backend.alert.AlertService;
import org.example.agroguardian_backend.alert.AlertRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelemetryConfig {

    @Bean
    public TelemetryService telemetryService(
            InfluxDBClient influxDBClient,
            AlertRule alertRule,
            AlertService alertService) {
        TelemetryService telemetryService = new TelemetryService();
        telemetryService.setInfluxDBClient(influxDBClient);
        telemetryService.setAlertRule(alertRule);
        telemetryService.setAlertService(alertService);
        return telemetryService;
    }
} 