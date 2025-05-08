package org.example.agroguardian_backend.alert;

import org.example.agroguardian_backend.firebase.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AlertService {

    @Autowired
    private AlertRepository repo;

    @Autowired
    private FirebaseService firebaseService;

    public void createAlert(String deviceName, String message) {
        Alert alertEntity = new Alert();
        alertEntity.setDeviceName(deviceName);
        alertEntity.setMessage(message);
        alertEntity.setCreatedAt(Instant.now());
        repo.save(alertEntity);
        firebaseService.sendNotification(deviceName, message);
    }

    public List<AlertDataDto> getAllAlerts(String deviceName) {
        List<Alert> alert = repo.findByDeviceName(deviceName);
        List<AlertDataDto> res =  new ArrayList<>();
        for (Alert alertEntity : alert) {
            res.add(convertToDto(alertEntity));
        }
        return res;
    }

    private AlertDataDto convertToDto(Alert alertEntity) {
        AlertDataDto alertDataDto = new AlertDataDto();
        alertDataDto.setId(alertEntity.getId());
        alertDataDto.setDeviceName(alertEntity.getDeviceName());
        alertDataDto.setMessage(alertEntity.getMessage());
        alertDataDto.setCreatedAt(alertEntity.getCreatedAt());
        return alertDataDto;
    }
}
