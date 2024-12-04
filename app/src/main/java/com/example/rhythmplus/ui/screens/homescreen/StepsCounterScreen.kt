package com.example.rhythmplus.ui.screens.homescreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rhythmplus.ui.screens.TopBar
import com.example.rhythmplus.ui.viewmodels.AppViewModelProvider
import com.example.rhythmplus.ui.viewmodels.home.StepsCounterViewModel

@Composable
fun StepsCounterScreen(
    navCtrl: NavController,
    stepsCounterVM: StepsCounterViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Scaffold(
        topBar = {
            TopBar(
                title = "",
                canNavigateBack = true,
                onNavigateUp = { navCtrl.navigateUp() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            Text("Steps counter Screen")
        }
    }
}