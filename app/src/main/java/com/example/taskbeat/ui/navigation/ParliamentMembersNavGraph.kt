package com.example.taskbeat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.taskbeat.ui.screens.EnumScreens
import com.example.taskbeat.ui.screens.MemberListScreen
import com.example.taskbeat.ui.screens.MemberScreen
import com.example.taskbeat.ui.screens.HomeScreen
import com.example.taskbeat.ui.screens.NoteScreen

@Composable
fun TaskBeatNavHost(
    navCtrl: NavHostController
) {
    NavHost(navController = navCtrl, startDestination = EnumScreens.HOME.name) {
        composable(route = EnumScreens.HOME.route) {
            HomeScreen(navCtrl)
        }
        composable(route = EnumScreens.MEMBERLIST.route) { backStackEntry ->
            MemberListScreen(navCtrl, backStackEntry)
        }
        composable(route = EnumScreens.MEMBER.route) {
            MemberScreen(navCtrl)
        }
        composable(route = EnumScreens.NOTE.route) {
            NoteScreen(navCtrl)
        }
    }
}