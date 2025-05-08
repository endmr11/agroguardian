package org.example.agroguardian_backend.telemetry;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxTable;
import lombok.Setter;
import org.example.agroguardian_backend.alert.AlertService;
import org.example.agroguardian_backend.alert.AlertRule;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Setter
@Service
public class TelemetryService {

    private InfluxDBClient influxDBClient;
    private AlertRule alertRule;
    private AlertService alertService;

    public void saveTelemetryToInfluxDB(String jsonPayload) {
        JSONObject json = new JSONObject(jsonPayload);
        try (WriteApi writeApi = influxDBClient.getWriteApi()) {
            Point point = Point.measurement("sensor_data")
                    .addTag("device_name", json.get("device_name").toString())
                    .addField("temperature", json.getDouble("temperature"))
                    .addField("humidity", json.getDouble("humidity"))
                    .addField("soil_moisture", json.getDouble("soil_moisture"))
                    .time(Instant.now(), WritePrecision.MS);

            writeApi.writePoint(point);
        } catch (Exception e) {
            System.out.println("InfluxDB'ye yazma hatas<UNK>: " + e.getMessage());
        }
        alertRule.check(json.getDouble("temperature"), json.getDouble("humidity"), json.getDouble("soil_moisture"))
                .ifPresent(alertMessage -> alertService.createAlert(json.get("device_name").toString(), alertMessage));
    }

    public List<TelemetryDto> getAllTelemetries(String deviceName) {
        String query = String.format("""
        from(bucket: "agroguardian-bucket")
                      |> range(start: -3m)
                      |> filter(fn: (r) => r["_measurement"] == "sensor_data")
                      |> filter(fn: (r) => r["device_name"] == "device-001")
                      |> filter(fn: (r) => r["_field"] == "soil_moisture" or r["_field"] == "humidity" or r["_field"] == "temperature")
                      |> pivot(rowKey:["_time"], columnKey: ["_field"], valueColumn: "_value")
    """, deviceName);

        List<FluxTable> tables = influxDBClient.getQueryApi().query(query);

        return tables.stream()
                .flatMap(table -> table.getRecords().stream())
                .map(record -> {
                    TelemetryDto data = new TelemetryDto();
                    data.setDeviceName(deviceName);
                    data.setTemperature(Double.parseDouble(Objects.requireNonNull(record.getValueByKey("temperature")).toString()));
                    data.setHumidity((Double.parseDouble(Objects.requireNonNull(record.getValueByKey("humidity")).toString())));
                    data.setSoilMoisture(Double.parseDouble(Objects.requireNonNull(record.getValueByKey("soil_moisture")).toString()));
                    data.setTimestamp(record.getTime());
                    return data;
                })
                .collect(Collectors.toList());
    }


    private Double getDouble(Object value) {
        return value != null ? Double.parseDouble(value.toString()) : null;
    }
}
