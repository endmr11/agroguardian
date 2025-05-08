package com.example.agroguardianappandroid.data.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agroguardianappandroid.data.models.CommandData
import com.example.agroguardianappandroid.data.models.TelemetryData
import com.example.agroguardianappandroid.data.remote.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    private val apiService = ApiService.create()

    private val _telemetryData = MutableStateFlow<List<TelemetryData>>(emptyList())
    val telemetryData: StateFlow<List<TelemetryData>> = _telemetryData

    private val _commandData = MutableStateFlow<List<CommandData>>(emptyList())
    val commandData: StateFlow<List<CommandData>> = _commandData

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val maxDataPoints = 50

    val downsampledData: List<TelemetryData>
        get() {
            val data = _telemetryData.value
            if (data.size <= maxDataPoints) return data

            val step = data.size / maxDataPoints
            return data.filterIndexed { index, _ -> index % step == 0 }
        }

    fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val telemetry = apiService.fetchTelemetry("device-001")
                _telemetryData.value = telemetry.sortedBy { it.time }

                val commands = apiService.fetchLastCommands("device-001")
                _commandData.value = commands.sortedBy { it.time }
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error loading data: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}