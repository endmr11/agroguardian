package com.example.agroguardianappandroid.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agroguardianappandroid.data.models.CommandResponse
import com.example.agroguardianappandroid.data.models.Telemetry
import com.example.agroguardianappandroid.data.mqtt.MqttManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val mqttManager = MqttManager(
        context = application,
        onTelemetryReceived = { _telemetry.value = it },
        onCommandResponseReceived = { handleCommandResponse(it) }
    )

    private val _telemetry = MutableStateFlow<Telemetry?>(null)
    val telemetry: StateFlow<Telemetry?> = _telemetry

    private val _showMessage = MutableStateFlow(false)
    val showMessage: StateFlow<Boolean> = _showMessage

    private val _alertMessage = MutableStateFlow("")
    val alertMessage: StateFlow<String> = _alertMessage

    private val _showIrrigationAnim = MutableStateFlow(false)
    val showIrrigationAnim: StateFlow<Boolean> = _showIrrigationAnim

    private val _showVentilationAnim = MutableStateFlow(false)
    val showVentilationAnim: StateFlow<Boolean> = _showVentilationAnim

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    init {
        mqttManager.connect()
    }

    fun sendCommand(command: String) {
        if (!_isProcessing.value) {
            mqttManager.sendCommand(command)
        }
    }

    private fun handleCommandResponse(response: CommandResponse) {
        _showMessage.value = true
        _alertMessage.value = response.getAlertMessage()

        viewModelScope.launch {
            delay(3000)
            _showMessage.value = false
        }

        if (response.isIrrigationStarted()) {
            _showIrrigationAnim.value = true
            _isProcessing.value = true
        } else if (response.isIrrigationCompleted() || response.isIrrigationStopped()) {
            _isProcessing.value = false
            _showIrrigationAnim.value = false
        }

        if (response.isVentilationStarted() || response.isSoilVentilationStarted()) {
            _isProcessing.value = true
            _showVentilationAnim.value = true
        } else if (response.isVentilationCompleted() || response.isVentilationStopped() ||
            response.isSoilVentilationCompleted() || response.isSoilVentilationStopped()) {
            _isProcessing.value = false
            _showVentilationAnim.value = false
        }
    }

    override fun onCleared() {
        mqttManager.disconnect()
        super.onCleared()
    }
}