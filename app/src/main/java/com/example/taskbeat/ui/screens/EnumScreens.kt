package com.example.taskbeat.ui.screens

enum class EnumScreens(val route: String) {
    HOME("home"),
    SETTINGS("settings"),
    STEPS_COUNTER("steps_counter"),
    WORKOUT_TIME("workout_time"),
    HEART_RATE("heart_rate"),
    BODY_COMPOSITION("body_composition"),
    WATER("water"),
    BLOOD_PRESSURE("blood_pressure"),
    BLOOD_GLUCOSE("blood_glucose");

    fun withParams(vararg args: String): String {
        var updatedRoute = route
        args.forEach { updatedRoute = updatedRoute.replaceFirst(Regex("\\{[^}]+\\}"), it) }
        return updatedRoute
    }
}