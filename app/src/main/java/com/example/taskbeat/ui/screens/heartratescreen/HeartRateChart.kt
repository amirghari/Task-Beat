package com.example.taskbeat.ui.screens.heartratescreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import com.example.taskbeat.ui.viewmodels.HeartRateViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HeartRateChart(
    heartrateVM: HeartRateViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val healthData by heartrateVM.healthData.observeAsState(initial = null)
    val heartRateReadings = healthData?.heartRateReadings ?: emptyList()
    val timestamps = healthData?.timestamps ?: emptyList()

    if (heartRateReadings.isNotEmpty() && timestamps.isNotEmpty()) {
        // Group the readings by day and calculate the average heart rate for each day
        val formatter = SimpleDateFormat("MM/dd", Locale.getDefault())
        val dailyAverages = mutableMapOf<String, MutableList<Int>>()

        for (i in heartRateReadings.indices) {
            val dateKey = formatter.format(timestamps[i])
            dailyAverages.getOrPut(dateKey) { mutableListOf() }.add(heartRateReadings[i])
        }

        val averageHeartRates = dailyAverages.mapValues { (_, readings) ->
            readings.average().toFloat()
        }

        val labels = averageHeartRates.keys.toList()
        val averages = averageHeartRates.values.toList()

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            val spacing = 30f
            val barWidth = 60f
            val maxRate = 100f
            val minRate = 40f
            val yStepCount = 4
            val yInterval = (maxRate - minRate) / yStepCount
            val availableWidth = size.width - spacing * 2
            val xStep = availableWidth / (labels.size + 1)
            val yStep = (size.height - spacing) / (maxRate - minRate)

            // Draw horizontal lines
            for (i in 0..yStepCount) {
                val y = size.height - spacing - (i * yInterval * yStep)
                drawLine(
                    color = Color.Gray.copy(alpha = 0.4f),
                    start = Offset(spacing, y),
                    end = Offset(size.width - spacing, y),
                    strokeWidth = 1.dp.toPx()
                )

                // Draw Y-axis labels
                drawIntoCanvas { canvas ->
                    val label = (minRate + (i * yInterval)).toInt().toString()
                    canvas.nativeCanvas.drawText(
                        label,
                        spacing - 20f,
                        y + 5f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 32f
                            textAlign = android.graphics.Paint.Align.RIGHT
                        }
                    )
                }
            }

            // Draw bars for each day's average heart rate with rounded tops
            for (i in averages.indices) {
                val x = spacing + (i * (xStep-140)) + barWidth / 2
                val barHeight = (averages[i] - minRate) * yStep
                val y = size.height - spacing - barHeight

                drawRoundRect(
                    color = if (averages[i] < 60) Color(0xFF7EBD8F) else Color(0xFF7EBD8F),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(30f, 30f) // Rounded top, flat bottom
                )

                // Draw the value above the bar
                drawIntoCanvas { canvas ->
                    val label = averages[i].toInt().toString()
                    canvas.nativeCanvas.drawText(
                        label,
                        x + barWidth / 2,
                        y - 10f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 36f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }

            // Draw X-axis labels (dates) directly under the bars with same margin
            drawIntoCanvas { canvas ->
                for (i in labels.indices) {
                    val x = spacing + (i * (xStep-140)) + barWidth
                    val y = size.height + 10f
                    canvas.nativeCanvas.drawText(
                        labels[i],
                        x,
                        y,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 36f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
        }
    } else {
        Text(
            text = "No data available.",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}