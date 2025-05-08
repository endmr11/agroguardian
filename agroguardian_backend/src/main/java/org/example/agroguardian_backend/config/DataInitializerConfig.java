package org.example.agroguardian_backend.config;

import lombok.RequiredArgsConstructor;
import org.example.agroguardian_backend.device.Device;
import org.example.agroguardian_backend.device.DeviceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializerConfig {

    private final DeviceRepository deviceRepository;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Bean
    public ApplicationRunner dataInitializer() {
        return args -> {
            if (ddlAuto.equals("create")) {
                Device device = new Device();
                device.setName("device-001");
                device.setLocation("Istanbul");
                device.setToken("eq3jcHoATV-fZhRGp-Os37:APA91bG0Gf6ctTwq-9omWzxPx2dzEWPG41a-uRDiiYRKrYjsXlptxUiTutVFoTDI45I0XZUIgP8YYePqT2FGhl_uSKnUa3YJ7wPdUrF1YOHH6lCnXS9fZo8");
                deviceRepository.save(device);
                System.out.println("Başlangıç verileri başarıyla eklendi.");
            }

        };
    }
}
