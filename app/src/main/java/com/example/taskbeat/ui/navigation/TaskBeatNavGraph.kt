package com.example.taskbeat.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.taskbeat.ui.screens.*
import com.example.taskbeat.ui.screens.BloodGlucoseScreen
import com.example.taskbeat.ui.screens.BloodPressureScreen
import com.example.taskbeat.ui.screens.BodyCompositionScreen
import com.example.taskbeat.ui.screens.chatscreen.ChatScreen
import com.example.taskbeat.ui.screens.chatscreen.LoadingChatScreen
import com.example.taskbeat.ui.screens.EnumScreens
import com.example.taskbeat.ui.screens.SettingsScreen
import com.example.taskbeat.ui.screens.StepsCounterScreen
import com.example.taskbeat.ui.screens.WaterScreen
import com.example.taskbeat.ui.screens.WorkoutTimeScreen

@Composable
fun TaskBeatNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = EnumScreens.HOME.route,
        modifier = modifier
    ) {
        composable(EnumScreens.HOME.route) { HomeScreen(navController) }
        composable(EnumScreens.SIGN_IN.route) { SignInScreen(navController) }
        composable(EnumScreens.SETTINGS.route) { SettingsScreen(navController) }
        composable(EnumScreens.STEPS_COUNTER.route) { StepsCounterScreen(navController) }
        composable(EnumScreens.WORKOUT_TIME.route) { WorkoutTimeScreen(navController) }
        composable(EnumScreens.HEART_RATE.route) { HeartRateScreen(navController) }
        composable(EnumScreens.BODY_COMPOSITION.route) { BodyCompositionScreen(navController) }
        composable(EnumScreens.WATER.route) { WaterScreen(navController) }
        composable(EnumScreens.BLOOD_PRESSURE.route) { BloodPressureScreen(navController) }
        composable(EnumScreens.BLOOD_GLUCOSE.route) { BloodGlucoseScreen(navController) }

        composable(route = EnumScreens.LOADING_CHAT.route) {
            LoadingChatScreen(
                onModelLoaded = {
                    navController.navigate(EnumScreens.CHAT.route) {
                        popUpTo(EnumScreens.LOADING_CHAT.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = EnumScreens.CHAT.route) {
            ChatScreen(navController)
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navItems = listOf(
        EnumScreens.HOME,
        EnumScreens.HEART_RATE,
        EnumScreens.LOADING_CHAT,
        EnumScreens.BODY_COMPOSITION,
        EnumScreens.BLOOD_PRESSURE
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: ""

    NavigationBar {
        navItems.forEach { screen ->
            NavigationBarItem(
                icon = {
                    if (screen.icon != null) {
                        Icon(imageVector = screen.icon, contentDescription = screen.displayName)
                    }
                },
                label = { Text(screen.displayName) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(EnumScreens.HOME.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

