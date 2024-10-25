package com.example.taskbeat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.taskbeat.data.DataRepository
import com.example.taskbeat.ui.theme.TaskBeatTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    private lateinit var dataRepo: DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataRepo = (application as TaskBeatApplication).container.dataRepo

        setContent {
            val isDarkTheme by dataRepo.isDarkThemeFlow.collectAsState(initial = false)

            TaskBeatTheme(darkTheme = isDarkTheme) {
                TaskBeatApp()
            }
        }
    }
}