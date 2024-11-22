package com.example.taskbeat

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.taskbeat.ui.navigation.TaskBeatNavHost
import com.example.taskbeat.ui.navigation.BottomNavBar

/**
 * The main app composable that sets up the scaffold structure with a bottom navigation bar
 * and a navigation host for handling screen transitions.
 */
@Composable
fun TaskBeatApp(navCtrl: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = {
            BottomNavBar(navCtrl) // Add bottom navigation bar with 5 categories
        }
    ) { innerPadding ->
        TaskBeatNavHost(navCtrl, Modifier.padding(innerPadding)) // Main navigation host
    }
}

