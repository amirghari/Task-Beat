package com.example.taskbeat.ui.screens

enum class EnumScreens(val route: String) {
    HOME("home"),
    MEMBERLIST("memberlist/{type}/{selected}"),
    MEMBER("member/{param}"),
    NOTE("note/{param}");

    fun withParams(vararg args: String): String {
        var updatedRoute = route
        args.forEach { updatedRoute = updatedRoute.replaceFirst(Regex("\\{[^}]+\\}"), it) }
        return updatedRoute
    }
}