package org.example.agroguardian_backend.telemetry;

import lombok.Data;
import java.time.Instant;

@Data
public class TelemetryDto {
    private String deviceName;
    private double temperature;
    private double humidity;
    private double soilMoisture;
    private Instant timestamp;
}