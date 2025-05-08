// build.gradle dependencies:
// implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.agroguardianappandroid.data.models.TelemetryData
import java.text.SimpleDateFormat
import java.util.Locale
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun TelemetryChart(
    telemetryData: List<TelemetryData>,
    modifier: Modifier = Modifier
) {
    if (telemetryData.isEmpty()) return

    AndroidView(
        modifier = modifier,
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)

                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        return if (index >= 0 && index < telemetryData.size) {
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(telemetryData[index].time)
                        } else {
                            ""
                        }
                    }
                }

                axisLeft.setDrawGridLines(true)
                xAxis.setDrawGridLines(false)
                axisRight.isEnabled = false

                legend.form = Legend.LegendForm.LINE
                legend.textSize = 11f
                legend.textColor = Color.BLACK
                legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)
            }
        },
        update = { chart ->
            val temperatureEntries = telemetryData.mapIndexed { index, data ->
                Entry(index.toFloat(), data.temperature.toFloat())
            }

            val humidityEntries = telemetryData.mapIndexed { index, data ->
                Entry(index.toFloat(), data.humidity.toFloat())
            }

            val soilMoistureEntries = telemetryData.mapIndexed { index, data ->
                Entry(index.toFloat(), data.soilMoisture.toFloat())
            }

            val temperatureDataSet = LineDataSet(temperatureEntries, "Temperature").apply {
                color = Color.RED
                setCircleColor(Color.RED)
                lineWidth = 2f
                circleRadius = 3f
                setDrawCircleHole(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }

            val humidityDataSet = LineDataSet(humidityEntries, "Humidity").apply {
                color = Color.BLUE
                setCircleColor(Color.BLUE)
                lineWidth = 2f
                circleRadius = 3f
                setDrawCircleHole(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }

            val soilMoistureDataSet = LineDataSet(soilMoistureEntries, "Soil Moisture").apply {
                color = Color.GREEN
                setCircleColor(Color.GREEN)
                lineWidth = 2f
                circleRadius = 3f
                setDrawCircleHole(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }

            val lineData = LineData(temperatureDataSet, humidityDataSet, soilMoistureDataSet)
            chart.data = lineData
            chart.invalidate()
        }
    )
}