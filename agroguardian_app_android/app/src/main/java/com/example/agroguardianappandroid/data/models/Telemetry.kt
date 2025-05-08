package com.example.agroguardianappandroid.data.models

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName

data class Telemetry(
    @SerializedName("device_name")
    val deviceName: String,

    val temperature: Double,
    val humidity: Double,

    @SerializedName("soil_moisture")
    val soilMoisture: Double,

    val timestamp: String
) {
    fun temperatureColor(): Color {
        return when {
            temperature < 20 -> Color.Red
            temperature < 25 -> Color(0xFFF09F26)
            else -> Color.Green
        }
    }

    fun humidityColor(): Color {
        return when {
            humidity < 60 -> Color.Red
            humidity > 70 -> Color(0xFFF09F26)
            else -> Color.Green
        }
    }

    fun soilMoistureColor(): Color {
        return when {
            soilMoisture < 15 -> Color.Red
            soilMoisture > 60 -> Color(0xFFF09F26)
            else -> Color.Green
        }
    }
}