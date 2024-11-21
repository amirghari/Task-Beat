package com.example.taskbeat.ui.screens

import TopBar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskbeat.SettingViewModel
import com.example.taskbeat.data.DataRepository
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider

@Composable
fun SettingsScreen(
    navCtrl: DataRepository,
    settingsVM: SettingViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Scaffold(
        topBar = {
            TopBar(
                title = "Reminders",
                canNavigateBack = true,
                onNavigateUp = { navCtrl.navigateUp() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            Text("Settings Screen")
        }
    }
}