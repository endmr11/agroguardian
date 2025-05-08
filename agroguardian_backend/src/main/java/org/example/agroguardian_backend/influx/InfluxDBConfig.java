package org.example.agroguardian_backend.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfig {
    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create("http://localhost:8086", "1QGLR2iQlSkxVeuZJ4T2Q8XQZrSRIDXosjW4WbjPPXggQOGy0cPah_RDz78fnXYyzTlYmyfbiu5b3a_DNbNjSQ==".toCharArray(), "agroguardian", "agroguardian-bucket");
    }
}
