package com.example.taskbeat.data

import com.example.taskbeat.model.User
import com.example.taskbeat.model.Health
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    val isDarkThemeFlow: Flow<Boolean>

    suspend fun toggleTheme()

    suspend fun addUser(user: User)
    suspend fun addOrUpdateHealthData(health: Health)

    fun getUserByEmail(email: String): Flow<User?>
    fun getHealthDataByUserId(userId: Long): Flow<Health?>

    suspend fun deleteUserByEmail(email: String)
    suspend fun deleteHealthDataByUserId(userId: Long)

    suspend fun updateWaterIntake(userId: Long, waterIntake: Int)

    suspend fun updateBMI(userId: Long, bmi: Double)
}