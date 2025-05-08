package org.example.agroguardian_backend.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    public Device getDeviceByName(String deviceName) {
        return deviceRepository.findByName(deviceName);
    }
}
