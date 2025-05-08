package com.example.agroguardianappandroid.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.rive.runtime.kotlin.core.Fit
import com.example.agroguardianappandroid.R
import com.example.agroguardianappandroid.components.ComposableRiveAnimationView
import com.example.agroguardianappandroid.data.viewmodels.HomeViewModel
import kotlin.text.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    navigateToDetail: () -> Unit
) {
    val telemetry by viewModel.telemetry.collectAsState()
    val showMessage by viewModel.showMessage.collectAsState()
    val alertMessage by viewModel.alertMessage.collectAsState()
    val showIrrigationAnim by viewModel.showIrrigationAnim.collectAsState()
    val showVentilationAnim by viewModel.showVentilationAnim.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Device: device-001") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TelemetryCard(
                title = "Temperature:",
                value = telemetry?.temperature,
                color = telemetry?.temperatureColor() ?: Color.Gray
            )

            TelemetryCard(
                title = "Humidity:",
                value = telemetry?.humidity,
                color = telemetry?.humidityColor() ?: Color.Gray
            )

            TelemetryCard(
                title = "Soil Moisture:",
                value = telemetry?.soilMoisture,
                color = telemetry?.soilMoistureColor() ?: Color.Gray
            )

            Button(
                onClick = navigateToDetail,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("More...", style = MaterialTheme.typography.titleLarge)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (showIrrigationAnim) {
                    ComposableRiveAnimationView(
                        animation = R.raw.water,
                        fit = Fit.COVER,
                        modifier = Modifier
                            .size(100.dp)
                    )
                }

                if (showVentilationAnim) {
                    ComposableRiveAnimationView(
                        animation = R.raw.windmill,
                        fit = Fit.COVER,
                        modifier = Modifier
                            .size(100.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = showMessage,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = alertMessage,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ControlButton(
                    title = "Start Irrigation",
                    isProcessing = isProcessing,
                    onClick = { viewModel.sendCommand("START_IRRIGATION") },
                    modifier = Modifier.weight(1f)
                )

                ControlButton(
                    title = "Start Ventilation",
                    isProcessing = isProcessing,
                    onClick = { viewModel.sendCommand("START_VENTILATION") },
                    modifier = Modifier.weight(1f)
                )

                ControlButton(
                    title = "Start Soil Ventilation",
                    isProcessing = isProcessing,
                    onClick = { viewModel.sendCommand("START_SOIL_VENTILATION") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TelemetryCard(title: String, value: Double?, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Text(
                text = formatDouble(value),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun ControlButton(
    title: String,
    isProcessing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !isProcessing,
        modifier = modifier
            .height(55.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatDouble(value: Double?): String {
    return String.format("%.1f", value ?: 0.0)
}