package com.example.taskbeat.ui.navigation

import HeartRateScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.taskbeat.ui.screens.BloodGlucoseScreen
import com.example.taskbeat.ui.screens.BloodPressureScreen
import com.example.taskbeat.ui.screens.BodyCompositionScreen
import com.example.taskbeat.ui.screens.ChatScreen
import com.example.taskbeat.ui.screens.EnumScreens
import com.example.taskbeat.ui.screens.HomeScreen
import com.example.taskbeat.ui.screens.SettingsScreen
import com.example.taskbeat.ui.screens.StepsCounterScreen
import com.example.taskbeat.ui.screens.WaterScreen
import com.example.taskbeat.ui.screens.WorkoutTimeScreen


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
        composable(route = EnumScreens.STEPS_COUNTER.route) {
            StepsCounterScreen(navCtrl)
        }
        composable(route = EnumScreens.WORKOUT_TIME.route) {
            WorkoutTimeScreen(navCtrl)
        }
        composable(route = EnumScreens.HEART_RATE.route) {
            HeartRateScreen(navCtrl)
        }
        composable(route = EnumScreens.BODY_COMPOSITION.route) {
            BodyCompositionScreen(navCtrl)
        }
        composable(route = EnumScreens.WATER.route) {
            WaterScreen(navCtrl)
        }
        composable(route = EnumScreens.BLOOD_PRESSURE.route) {
            BloodPressureScreen(navCtrl)
        }
        composable(route = EnumScreens.BLOOD_GLUCOSE.route) {
            BloodGlucoseScreen(navCtrl)
        }
        composable(route = EnumScreens.CHAT.route) {
            ChatScreen(navCtrl)
        }
    }
}