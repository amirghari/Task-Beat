package com.example.taskbeat.ui.screens.homescreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import com.example.taskbeat.ui.viewmodels.home.BodyCompositionViewModel

@Composable
fun BodyCompositionScreen(
    navCtrl: NavController,
    bodyCompositionVM: BodyCompositionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val weightFromVM by bodyCompositionVM.weight.observeAsState()
    val heightFromVM by bodyCompositionVM.height.observeAsState()

    var weight by remember { mutableStateOf(56f) }
    var height by remember { mutableStateOf(170f) }

    LaunchedEffect(weightFromVM) {
        val weightValue = weightFromVM
        if (weightValue != null) {
            weight = weightValue.toFloat()
        }
    }

    LaunchedEffect(heightFromVM) {
        val heightValue = heightFromVM
        if (heightValue != null) {
            height = heightValue.toFloat()
        }
    }

    val heightInMeters = height / 100
    val bmiValue = if (heightInMeters > 0) weight / (heightInMeters * heightInMeters) else 0f

    // Observe BMI from ViewModel
    val bmi by bodyCompositionVM.bmi.observeAsState(0.0)

    // Observe currentUserId from ViewModel
    val userId by bodyCompositionVM.currentUserId.observeAsState()

    LaunchedEffect(bmiValue, weight, height, userId) {
        if (userId != null) {
            // Update BMI, weight, and height in ViewModel whenever they change and userId is available
            bodyCompositionVM.updateBMI(weight.toDouble(), height.toDouble(), bmiValue.toDouble())
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                // Title Text
                Text(
                    text = "Calculate Your BMI",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                // Weight Input Representation
                CircularInput(
                    label = "Weight",
                    value = weight,
                    unit = "kg",
                    onValueChange = { weight = it },
                    maxValue = 200f
                )

                // Height Input Representation
                CircularInput(
                    label = "Height",
                    value = height,
                    unit = "cm",
                    onValueChange = { height = it },
                    maxValue = 250f
                )

                // BMI Circular Display
                BMICircularDisplay(bmi = bmiValue)
            }
        }
    }
}

@Composable
fun CircularInput(
    label: String,
    value: Float,
    unit: String,
    onValueChange: (Float) -> Unit,
    maxValue: Float
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            // Draw the circular gauge background
            drawArc(
                color = Color.LightGray,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 30f, cap = StrokeCap.Round)
            )
            // Draw the progress arc
            drawArc(
                color = Color(0xFF7EBD8F),
                startAngle = 135f,
                sweepAngle = (value / maxValue) * 270f,
                useCenter = false,
                style = Stroke(width = 30f, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format("%.1f", value),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = unit,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Slider to adjust the value
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..maxValue,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .align(Alignment.BottomCenter),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF7EBD8F),
                activeTrackColor = Color(0xFF7EBD8F)
            )
        )
    }
}

@Composable
fun BMICircularDisplay(bmi: Float) {
    val circleColor = when {
        bmi < 18.5 -> Color.Blue
        bmi in 18.5f..24.9f -> Color(0xFF7EBD8F) // Green
        bmi in 25f..29.9f -> Color.Yellow
        bmi in 30f..34.9f -> Color(0xFFE59866)
        bmi in 35f..39.9f -> Color.Red
        bmi >= 40f -> Color(0xFFE53935)
        else -> Color.Gray
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(150.dp)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.size(150.dp)) {
            // Draw the circular gauge background
            drawArc(
                color = circleColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 26f, cap = StrokeCap.Round)
            )
        }

        Text(
            text = String.format("%.1f BMI", bmi),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
    }
}