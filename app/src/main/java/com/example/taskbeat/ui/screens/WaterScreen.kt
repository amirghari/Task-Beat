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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskbeat.R
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import com.example.taskbeat.ui.viewmodels.WaterViewModel

@Composable
fun WaterScreen(
    navCtrl: WaterViewModel,
    waterVM: WaterViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val dailyGoal = 2000 // in mL
    var waterIntake by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopBar(
                title = "Water Intake Tracker",
                canNavigateBack = true,
                onNavigateUp = { navCtrl.navigateUp() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),  // Use theme-aware background color
            contentAlignment = Alignment.Center
        ) {
            WaterIntakeContent(
                waterIntake = waterIntake,
                dailyGoal = dailyGoal,
                onAddWater = { waterIntake += 250 },
                onRemoveWater = { if (waterIntake >= 250) waterIntake -= 250 }
            )
        }
    }
}

@Composable
fun WaterIntakeContent(
    waterIntake: Int,
    dailyGoal: Int,
    onAddWater: () -> Unit,
    onRemoveWater: () -> Unit
) {
    val progress = (waterIntake / dailyGoal.toFloat()).coerceIn(0f, 1f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.water_bottle),
            contentDescription = "Water Glass Icon",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Water Intake Text
        Text(
            text = "$waterIntake mL",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Circular Progress Indicator
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(165.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 10.dp
            )
            Text(
                text = "${(progress * 100).toInt()}% of Daily Goal",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Buttons to Add/Remove Water Intake
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onRemoveWater,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("-250 mL", color = MaterialTheme.colorScheme.onPrimary)
            }

            Button(
                onClick = onAddWater,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("+250 mL", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
