package com.example.rhythmplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.rhythmplus.data.DataRepository
import com.example.rhythmplus.ui.theme.RhythmPlusTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    private lateinit var dataRepo: DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataRepo = (application as RhythmPlusApplication).container.dataRepo

        setContent {
            val isDarkTheme by dataRepo.isDarkThemeFlow.collectAsState(initial = false)

            RhythmPlusTheme(darkTheme = isDarkTheme) {
                RhythmPlusApp()
            }
        }
    }
}