package com.example.rhythmplus.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rhythmplus.R
import com.example.rhythmplus.ui.viewmodels.AppViewModelProvider
import com.example.rhythmplus.ui.viewmodels.BloodPressureViewModel

@Composable
fun BloodPressureScreen(
    navCtrl: NavController,
    bloodPressureVM: BloodPressureViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var systolic by remember { mutableStateOf("") }
    var diastolic by remember { mutableStateOf("") }

    // Observing last recorded blood pressure value from ViewModel
    val lastRecorded by bloodPressureVM.lastRecorded.observeAsState()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            BloodPressureContent(
                systolic = systolic,
                diastolic = diastolic,
                onSystolicChange = { systolic = it },
                onDiastolicChange = { diastolic = it },
                onSavePressure = {
                    if (systolic.isNotBlank() && diastolic.isNotBlank()) {
                        bloodPressureVM.saveBloodPressure(systolic, diastolic)
                        systolic = ""
                        diastolic = ""
                    }
                },
                lastRecorded = lastRecorded
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
    onSavePressure: () -> Unit,
    lastRecorded: Pair<String, String>?
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
        OutlinedTextField(
            value = systolic,
            onValueChange = onSystolicChange,
            label = { Text("Systolic (mmHg)") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = diastolic,
            onValueChange = onDiastolicChange,
            label = { Text("Diastolic (mmHg)") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display Last Recorded Blood Pressure
        lastRecorded?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color(0xFFE8F5E9), shape = CircleShape)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.bc),
                    contentDescription = "Blood Glucose Icon",
                    tint = Color(0xFFDE7C7D),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${it.first} / ${it.second} mmHg",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7EBD8F)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }


        // Save Button
        Button(
            onClick = onSavePressure,
            modifier = Modifier.size(width = 200.dp, height = 56.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7EBD8F))
        ) {
            Text("Save", color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}