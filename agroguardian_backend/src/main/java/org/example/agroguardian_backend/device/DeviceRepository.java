package org.example.agroguardian_backend.device;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, String> {
    Device findByName(String name);
}
