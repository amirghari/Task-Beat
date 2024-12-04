package com.example.rhythmplus.data

import com.example.rhythmplus.model.User
import com.example.rhythmplus.model.Health
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface DataRepository {
    val isDarkThemeFlow: Flow<Boolean>

    suspend fun toggleTheme()

    suspend fun addUser(user: User)
    suspend fun addOrUpdateHealthData(health: Health)

    fun getUserByEmail(email: String): Flow<User?>
    fun getHealthDataByUserId(userId: Long): Flow<Health?>

    suspend fun deleteUserByEmail(email: String)
    suspend fun deleteHealthDataByUserId(userId: Long)

    suspend fun insertHealthData(health: Health)
    suspend fun updateHealthData(health: Health)
    suspend fun updateHeartRateData(userId: Long, heartRateReadings: List<Int>, timestamps: List<Date>)


    suspend fun updateWaterIntake(userId: Long, waterIntake: Int)

    suspend fun updateBMI(userId: Long, bmi: Double)

    suspend fun updateBloodGlucose(userId: Long, bloodGlucose: Double)
}