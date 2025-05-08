package org.example.agroguardian_backend.alert;

import lombok.Data;

import java.time.Instant;

@Data
public class AlertDataDto {
    private Long id;
    private String deviceName;
    private String message;
    private Instant createdAt;
}
