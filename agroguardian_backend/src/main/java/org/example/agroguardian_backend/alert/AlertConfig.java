package org.example.agroguardian_backend.alert;

import org.example.agroguardian_backend.firebase.FirebaseService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlertConfig {

    @Bean
    public AlertService alertService(AlertRepository alertRepository, FirebaseService firebaseService) {
        AlertService alertService = new AlertService();
        return alertService;
    }

    @Bean
    public AlertRule alertRule() {
        return new AlertRule();
    }
} 