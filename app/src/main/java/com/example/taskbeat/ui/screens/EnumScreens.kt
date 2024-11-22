package com.example.taskbeat.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * EnumScreens defines the routes, display names, and icons for each screen.
 */
enum class EnumScreens(val route: String, val displayName: String, val icon: ImageVector?) {
    HOME("home", "Home", Icons.Filled.Home),
    SETTINGS("settings", "Settings", Icons.Filled.Settings),
    STEPS_COUNTER("steps_counter", "Steps", Icons.Filled.KeyboardArrowUp),
    WORKOUT_TIME("workout_time", "Workout", Icons.Filled.PlayArrow),
    HEART_RATE("heart_rate", "Heart Rate", Icons.Filled.Favorite),
    BODY_COMPOSITION("body_composition", "BMI", Icons.Filled.Face),
    WATER("water", "Water", Icons.Filled.Star),
    BLOOD_PRESSURE("blood_pressure", "BP", Icons.Filled.Info),
    BLOOD_GLUCOSE("blood_glucose", "Glucose", Icons.Filled.AddCircle),
    SIGN_IN("sign_in", "Sign In", Icons.Filled.AccountCircle);

    /**
     * Generate a route with parameters by replacing placeholders.
     */
    fun withParams(vararg args: String): String {
        var updatedRoute = route
        args.forEach { updatedRoute = updatedRoute.replaceFirst(Regex("\\{[^}]+\\}"), it) }
        return updatedRoute
    }
}
