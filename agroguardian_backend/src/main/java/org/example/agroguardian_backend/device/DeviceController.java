package org.example.agroguardian_backend.device;

import org.example.agroguardian_backend.alert.AlertDataDto;
import org.example.agroguardian_backend.command.Command;
import org.example.agroguardian_backend.command.CommandRequest;
import org.example.agroguardian_backend.command.CommandService;
import org.example.agroguardian_backend.mqtt.MqttService;
import org.example.agroguardian_backend.alert.AlertService;
import org.example.agroguardian_backend.telemetry.TelemetryDto;
import org.example.agroguardian_backend.telemetry.TelemetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;


@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    @Autowired
    private MqttService mqttService;
    @Autowired
    private TelemetryService telemetryService;
    @Autowired
    private AlertService alertService;
    @Autowired
    private CommandService commandService;

    @GetMapping("/{deviceName}/telemetry")
    public List<TelemetryDto> getTelemetry(@PathVariable String deviceName) {
        return telemetryService.getAllTelemetries(deviceName);
    }

    @PostMapping("/{deviceName}/commands")
    public void sendCommand(@PathVariable String deviceName, @RequestBody CommandRequest commandRequest) {
        System.out.println(commandRequest);
        Command command = new Command();
        command.setType(commandRequest.getType());
        command.setTimestamp(Instant.now());
        commandService.create(command);
        mqttService.sendCommand(deviceName, commandRequest.getType());
    }

    @GetMapping("/{deviceName}/commands")
    public List<Command> getCommands(@PathVariable String deviceName) {
        return commandService.getTopThreeCommands();
    }


    @GetMapping("/{deviceName}/alerts")
    public List<AlertDataDto> getAllAlerts(@PathVariable String deviceName) {
        return alertService.getAllAlerts(deviceName);
    }
}
