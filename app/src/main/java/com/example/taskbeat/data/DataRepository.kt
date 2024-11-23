package com.example.taskbeat.data

import com.example.taskbeat.model.User
import com.example.taskbeat.model.Health
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    val isDarkThemeFlow: Flow<Boolean>

    suspend fun toggleTheme()

    suspend fun addUser(user: User)
    suspend fun addHealthData(health: Health)

    fun getUserByEmail(email: String): Flow<User?>
    fun getHealthDataByUserId(userId: Int): Flow<Health?>

    suspend fun updateHealthData(userId: Int, health: Health)

    suspend fun deleteUserByEmail(email: String)
    suspend fun deleteHealthDataByUserId(userId: Int)
}