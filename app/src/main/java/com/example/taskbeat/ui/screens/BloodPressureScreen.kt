package com.example.taskbeat.ui.screens

import TopBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taskbeat.R
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import com.example.taskbeat.ui.viewmodels.BloodPressureViewModel

@Composable
fun BloodPressureScreen(
    navCtrl: NavController,
    bloodPressureVM: BloodPressureViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var systolic by remember { mutableStateOf("") }
    var diastolic by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(
                title = "Blood Pressure Tracker",
                canNavigateBack = true,
                onNavigateUp = { navCtrl.navigateUp() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFE3F2FD)),
            contentAlignment = Alignment.Center
        ) {
            BloodPressureContent(
                systolic = systolic,
                diastolic = diastolic,
                onSystolicChange = { systolic = it },
                onDiastolicChange = { diastolic = it },
                onSavePressure = {  }
            )
        }
    }
}

@Composable
fun BloodPressureContent(
    systolic: String,
    diastolic: String,
    onSystolicChange: (String) -> Unit,
    onDiastolicChange: (String) -> Unit,
    onSavePressure: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.blood_pressure_gauge),
            contentDescription = "Blood Pressure Monitor Icon",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Blood Pressure Input Fields
        TextField(
            value = systolic,
            onValueChange = onSystolicChange,
            label = { Text("Systolic (mmHg)") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = diastolic,
            onValueChange = onDiastolicChange,
            label = { Text("Diastolic (mmHg)") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                onSavePressure() // Implement saving logic here
                onSystolicChange("") // Reset input after saving
                onDiastolicChange("")
            },
            modifier = Modifier.size(width = 200.dp, height = 56.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
        ) {
            Text("Save Blood Pressure", color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Last Recorded: -- / -- mmHg",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E88E5)
        )
    }
}
