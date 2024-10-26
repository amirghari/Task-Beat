package com.example.taskbeat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.taskbeat.ui.screens.EnumScreens
import com.example.taskbeat.ui.screens.HeartRateScreen

import com.example.taskbeat.ui.screens.HomeScreen
import com.example.taskbeat.ui.screens.ReminderScreen
import com.example.taskbeat.ui.screens.RemindersListScreen
import com.example.taskbeat.ui.screens.SettingsScreen


@Composable
fun TaskBeatNavHost(
    navCtrl: NavHostController
) {
    NavHost(navController = navCtrl, startDestination = EnumScreens.HOME.name) {
        composable(route = EnumScreens.HOME.route) {
            HomeScreen(navCtrl)
        }
        composable(route = EnumScreens.SETTINGS.route) {
            SettingsScreen(navCtrl)
        }
        composable(route = EnumScreens.REMINDERS_LIST.route) { backStackEntry ->
            RemindersListScreen(navCtrl, backStackEntry)
        }
        composable(route = EnumScreens.REMINDER_DETAILS.route) {
            ReminderScreen(navCtrl)
        }
        composable(route = EnumScreens.HEART_RATE.route) {
            HeartRateScreen(navCtrl)
        }
    }
}