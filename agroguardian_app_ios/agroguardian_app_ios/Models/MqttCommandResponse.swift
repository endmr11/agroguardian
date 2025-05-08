//
//  CommandResponse.swift
//  agroguardian_app_ios
//
//  Created by Eren on 6.05.2025.
//

struct MqttCommandResponse: Codable {
    let status: String
    
    
    func isIrrigationStarted() -> Bool {
        return status == "IRRIGATION_STARTED"
    }
    
    func isIrrigationCompleted() -> Bool {
        return status == "IRRIGATION_COMPLETED"
    }
    
    func isIrrigationStopped() -> Bool {
        return status == "IRRIGATION_STOPPED"
    }
    
    func isVentilationStarted() -> Bool {
        return status == "VENTILATION_STARTED"
    }
    
    func isVentilationCompleted() -> Bool {
        return status == "VENTILATION_COMPLETED"
    }
    
    func isVentilationStopped() -> Bool {
        return status == "VENTILATION_STOPPED"
    }
    
    func isSoilVentilationStarted() -> Bool {
        return status == "SOIL_VENTILATION_STARTED"
    }
    
    func isSoilVentilationCompleted() -> Bool {
        return status == "SOIL_VENTILATION_COMPLETED"
    }
    
    func isSoilVentilationStopped() -> Bool {
        return status == "SOIL_VENTILATION_STOPPED"
    }
    
    func getAlertMessage() -> String {
        if isIrrigationStarted() {
            return "Irrigation Started"
        } else if isIrrigationCompleted() {
            return "Irrigation Completed"
        } else if isIrrigationStopped() {
            return "Irrigation Stopped"
        } else if isVentilationStarted() {
            return "Ventilation Started"
        } else if isVentilationCompleted() {
            return "Ventilation Completed"
        } else if isVentilationStopped() {
            return "Ventilation Stopped"
        } else if isSoilVentilationStarted() {
            return "Soil Ventilation Started"
        } else if isSoilVentilationCompleted() {
            return "Soil Ventilation Completed"
        } else if isSoilVentilationStopped() {
            return "Soil Ventilation Stopped"
        } else {
            return "Unknown Command"
        }
    }
}
