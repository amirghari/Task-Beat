package com.example.rhythmplus

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.rhythmplus.ui.navigation.BottomNavBar
import com.example.rhythmplus.ui.navigation.RhythmPlusNavHost

/**
 * The main app composable that sets up the scaffold structure with a bottom navigation bar
 * and a navigation host for handling screen transitions.
 */
@Composable
fun RhythmPlusApp(navCtrl: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = {
            BottomNavBar(navCtrl)
        }
    ) { innerPadding ->
        RhythmPlusNavHost(navCtrl, Modifier.padding(innerPadding)) // Main navigation host
    }
}

