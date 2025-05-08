package com.example.agroguardianappandroid.data.models

data class CommandResponse(
    val status: String
) {
    fun isIrrigationStarted() = status == "IRRIGATION_STARTED"
    fun isIrrigationCompleted() = status == "IRRIGATION_COMPLETED"
    fun isIrrigationStopped() = status == "IRRIGATION_STOPPED"

    fun isVentilationStarted() = status == "VENTILATION_STARTED"
    fun isVentilationCompleted() = status == "VENTILATION_COMPLETED"
    fun isVentilationStopped() = status == "VENTILATION_STOPPED"

    fun isSoilVentilationStarted() = status == "SOIL_VENTILATION_STARTED"
    fun isSoilVentilationCompleted() = status == "SOIL_VENTILATION_COMPLETED"
    fun isSoilVentilationStopped() = status == "SOIL_VENTILATION_STOPPED"

    fun getAlertMessage(): String {
        return when {
            isIrrigationStarted() -> "Irrigation Started"
            isIrrigationCompleted() -> "Irrigation Completed"
            isIrrigationStopped() -> "Irrigation Stopped"
            isVentilationStarted() -> "Ventilation Started"
            isVentilationCompleted() -> "Ventilation Completed"
            isVentilationStopped() -> "Ventilation Stopped"
            isSoilVentilationStarted() -> "Soil Ventilation Started"
            isSoilVentilationCompleted() -> "Soil Ventilation Completed"
            isSoilVentilationStopped() -> "Soil Ventilation Stopped"
            else -> "Unknown Command"
        }
    }
}