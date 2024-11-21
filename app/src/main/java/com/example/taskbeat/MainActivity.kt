package com.example.taskbeat

import HeartRateScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskbeat.data.DataRepository
import com.example.taskbeat.ui.screens.BloodPressureScreen
import com.example.taskbeat.ui.screens.BodyCompositionScreen
import com.example.taskbeat.ui.screens.SettingsScreen
import com.example.taskbeat.ui.screens.WaterScreen
import com.example.taskbeat.ui.theme.TaskBeatTheme
import com.example.taskbeat.ui.viewmodels.BloodPressureViewModel
import com.example.taskbeat.ui.viewmodels.BodyCompositionViewModel
import com.example.taskbeat.ui.viewmodels.HeartRateViewModel
import com.example.taskbeat.ui.viewmodels.WaterViewModel


class MainActivity : ComponentActivity() {
    private lateinit var dataRepo: DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataRepo = (application as TaskBeatApplication).container.dataRepo

        setContent {
            val isDarkTheme by dataRepo.isDarkThemeFlow.collectAsState(initial = false)

            TaskBeatTheme(darkTheme = isDarkTheme) {
                MainScreen<Any>(
                    dataRepo = dataRepo,
                    heartRateViewModel = viewModel(),
                    bloodPressureViewModel = viewModel(),
                    bodyCompositionViewModel = viewModel(),
                    waterViewModel = viewModel(),
                    settingViewModel = viewModel()
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedScreen: String, onScreenSelected: Any) {

}

@Composable
fun MainScreen(
    dataRepo: DataRepository,
    heartRateViewModel: HeartRateViewModel,
    bloodPressureViewModel: BloodPressureViewModel,
    bodyCompositionViewModel: BodyCompositionViewModel,
    waterViewModel: WaterViewModel,
    settingViewModel: SettingViewModel // No longer generic
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = "HeartRate",
                onScreenSelected = { screen ->
                    when (screen) {
                        "HeartRate" -> navController.navigate("heartRateScreen")
                        "BloodPressure" -> navController.navigate("bloodPressureScreen")
                        "BodyComposition" -> navController.navigate("bodyCompositionScreen")
                        "Water" -> navController.navigate("waterScreen")
                        "Settings" -> navController.navigate("settingsScreen")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = "heartRateScreen"
            ) {
                composable("heartRateScreen") {
                    HeartRateScreen(heartRateViewModel)
                }
                composable("bloodPressureScreen") {
                    BloodPressureScreen(
                        navCtrl = navController,
                        bloodPressureVM = bloodPressureViewModel
                    )
                }
                composable("bodyCompositionScreen") {
                    BodyCompositionScreen(bodyCompositionViewModel)
                }
                composable("waterScreen") {
                    WaterScreen(waterViewModel)
                }
                composable("settingsScreen") {
                    SettingsScreen(dataRepo, settingViewModel)
                }
            }
        }
    }
}


@Composable
fun BottomNavigationBar(selectedScreen: String, onScreenSelected: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Heart Rate") },
            label = { Text("Heart Rate") },
            selected = selectedScreen == "HeartRate",
            onClick = { onScreenSelected("HeartRate") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Blood Pressure") },
            label = { Text("Blood Pressure") },
            selected = selectedScreen == "BloodPressure",
            onClick = { onScreenSelected("BloodPressure") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Body Composition") },
            label = { Text("Body Composition") },
            selected = selectedScreen == "BodyComposition",
            onClick = { onScreenSelected("BodyComposition") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Refresh, contentDescription = "Water Intake") },
            label = { Text("Water") },
            selected = selectedScreen == "Water",
            onClick = { onScreenSelected("Water") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = selectedScreen == "Settings",
            onClick = { onScreenSelected("Settings") }
        )
    }
}
