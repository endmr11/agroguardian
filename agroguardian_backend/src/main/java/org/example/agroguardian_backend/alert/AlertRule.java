package org.example.agroguardian_backend.alert;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AlertRule {

    public Optional<String> check(double temp, double hum, double soil) {
        if (hum < 50)
            return Optional.of("Humidity is too low! Watering may be required.");
        if (hum > 65)
            return Optional.of("Humidity is too high! The greenhouse must be ventilated.");
        if (temp > 30)
            return Optional.of("The temperature is too high! The greenhouse must be ventilated.");
        if (temp < 20)
            return Optional.of("The temperature is too low! The greenhouse must be heated\n.");
        if (soil < 15)
            return Optional.of("Soil moisture is too low! Watering may be required.");
        if (soil > 60)
            return Optional.of("Soil moisture is too high! Irrigation should be stopped.");
        return Optional.empty();
    }
}

