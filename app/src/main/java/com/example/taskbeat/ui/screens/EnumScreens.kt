package com.example.taskbeat.ui.screens

enum class EnumScreens(val route: String) {
    HOME("home"),
    SETTINGS("settings"),
    REMINDERS_LIST("reminders_list/{param}"),
    REMINDER_DETAILS("reminder_details/{param}"),
    HEART_RATE("heart_rate/{param}");

    fun withParams(vararg args: String): String {
        var updatedRoute = route
        args.forEach { updatedRoute = updatedRoute.replaceFirst(Regex("\\{[^}]+\\}"), it) }
        return updatedRoute
    }
}