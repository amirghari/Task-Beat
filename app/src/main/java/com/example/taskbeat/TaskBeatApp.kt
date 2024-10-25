package com.example.taskbeat

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.taskbeat.ui.navigation.TaskBeatNavHost

@Composable
fun TaskBeatApp(navCtrl: NavHostController = rememberNavController()) {
    TaskBeatNavHost(navCtrl)
}