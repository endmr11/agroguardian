package com.example.agroguardianappandroid.data.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TelemetryData(
    val deviceName: String,
    val temperature: Double,
    val humidity: Double,
    val soilMoisture: Double,
    val timestamp: String
) {
    private val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())

    val time: Date
        get() {
            return try {
                isoFormatter.parse(timestamp) ?: Date()
            } catch (e: Exception) {
                Date()
            }
        }
}