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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
    Scaffold(
        topBar = {
            TopBar(
                title = "",
                canNavigateBack = false,
                onNavigateUp = { navCtrl.navigateUp() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { navCtrl.navigate(EnumScreens.REMINDERS_LIST.route) }
                        .background(MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Add a reminder")
                }

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { navCtrl.navigate(EnumScreens.HEART_RATE.route) }
                        .background(MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Heart Rate")
                }

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { navCtrl.navigate(EnumScreens.SETTINGS.route) }
                        .background(MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Settings")
                }
            }
        }
    }
}