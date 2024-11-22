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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.taskbeat.R
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import com.example.taskbeat.ui.viewmodels.BloodGlucoseViewModel

@Composable
fun BloodGlucoseScreen(
    navCtrl: NavController,
    bloodGlucoseVM: BloodGlucoseViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var glucoseLevel by remember { mutableStateOf("") }

    // Observe the last recorded glucose level from the ViewModel
    val lastRecorded by bloodGlucoseVM.lastRecorded.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                title = "Blood Glucose Tracker",
                canNavigateBack = true,
                onNavigateUp = { navCtrl.navigateUp() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            BloodGlucoseContent(
                glucoseLevel = glucoseLevel,
                onGlucoseChange = { glucoseLevel = it },
                onSaveGlucose = {
                    if (glucoseLevel.isNotBlank()) {
                        bloodGlucoseVM.saveGlucoseLevel(glucoseLevel)
                        glucoseLevel = "" // Clear input after saving
                    }
                },
                lastRecorded = lastRecorded
            )
        }
    }
}

@Composable
fun BloodGlucoseContent(
    glucoseLevel: String,
    onGlucoseChange: (String) -> Unit,
    onSaveGlucose: () -> Unit,
    lastRecorded: String?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.blood_glucose_icon),
            contentDescription = "Blood Glucose Monitor Icon",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Blood Glucose Input Field
        TextField(
            value = glucoseLevel,
            onValueChange = onGlucoseChange,
            label = { Text("Glucose Level (mg/dL)") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                onSaveGlucose() // Call the save logic from the parent
            },
            modifier = Modifier.size(width = 200.dp, height = 56.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7EBD8F))
        ) {
            Text("Save Glucose Level", color = Color.White, fontWeight = FontWeight.Bold)

        }

        Spacer(modifier = Modifier.height(32.dp))

        // Display Last Recorded Glucose Level
        Text(
            text = "Last Recorded: ${lastRecorded ?: "--"} mg/dL",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7EBD8F)
        )
    }
}

