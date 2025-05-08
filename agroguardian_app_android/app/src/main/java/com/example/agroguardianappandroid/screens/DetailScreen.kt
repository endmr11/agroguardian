package com.example.agroguardianappandroid.screens
import TelemetryChart
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agroguardianappandroid.data.viewmodels.DetailViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = viewModel()
) {
    val telemetryData by viewModel.telemetryData.collectAsState()
    val commandData by viewModel.commandData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Loading data...",
                    modifier = Modifier.padding(top = 60.dp)
                )
            }
        } else {
            TelemetryChart(
                telemetryData = viewModel.downsampledData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(vertical = 16.dp)
            )

            Text(
                text = "Last 3 Data Points",
                style = MaterialTheme.typography.titleMedium
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                telemetryData.take(3).forEach { data ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(data.time),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = String.format("%.1f°C", data.temperature),
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = String.format("%.0f%%", data.humidity),
                            color = Color.Blue,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = String.format("%.0f%%", data.soilMoisture),
                            color = Color.Green,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Text(
                text = "Last 3 Commands",
                style = MaterialTheme.typography.titleMedium
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                commandData.take(3).forEach { data ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(data.time),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = data.type,
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Divider()
                }
            }
        }
    }
}