package com.example.taskbeat.ui.screens

import TopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taskbeat.data.DataRepository
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.example.taskbeat.ui.viewmodels.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navCtrl: NavController,
    homeVM: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val screens = listOf(
        EnumScreens.STEPS_COUNTER,
        EnumScreens.WORKOUT_TIME,
        EnumScreens.HEART_RATE,
        EnumScreens.BODY_COMPOSITION,
        EnumScreens.WATER,
        EnumScreens.BLOOD_PRESSURE,
        EnumScreens.BLOOD_GLUCOSE,
        EnumScreens.SETTINGS,
    )

    Scaffold(
        topBar = {
            TopBar(
                title = "",
                canNavigateBack = false,
                onNavigateUp = { navCtrl.navigateUp() }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
        ) {
            items(screens) { screen ->
                HomeItemBox(screen.route) { navCtrl.navigate(screen.route) }
            }
        }
    }
}

@Composable
fun HomeItemBox(
    screen: String,
    navigateToScreen: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .size(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { navigateToScreen() }
            .background(MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(screen)
    }
}