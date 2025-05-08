//
//  Telemetry.swift
//  agroguardian_app_ios
//
//  Created by Eren on 8.05.2025.
//

import SwiftUICore

struct Telemetry: Codable {
    let deviceName: String
    let temperature: Double
    let humidity: Double
    let soilMoisture: Double
    let timestamp: String

    private enum CodingKeys: String, CodingKey {
        case deviceName = "device_name"
        case temperature, humidity, soilMoisture = "soil_moisture", timestamp
    }
    
    func temperatureColor() -> Color {
        if temperature < 20 {
            return .red
        } else if temperature < 25 {
            return .yellow
        } else {
            return .green
        }
    }
    
    func humidityColor() -> Color {
        if humidity < 60 {
            return .red
        } else if humidity > 70 {
            return .yellow
        } else {
            return .green
        }
    }
    
    func soilMoistureColor() -> Color {
        if soilMoisture < 15 {
            return .red
        } else if soilMoisture > 60 {
            return .yellow
        } else {
            return .green
        }
    }
}
