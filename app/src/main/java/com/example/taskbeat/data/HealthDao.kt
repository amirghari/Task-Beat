package com.example.taskbeat.data

import androidx.room.*
import com.example.taskbeat.model.Health
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface HealthDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthData(health: Health)

    @Query("SELECT * FROM health WHERE user_id = :userId")
    fun getHealthDataByUserId(userId: Long): Flow<Health?>

    @Update
    suspend fun updateHealthData(health: Health)

    @Query("DELETE FROM health WHERE user_id = :userId")
    suspend fun deleteHealthDataByUserId(userId: Long)

    @Query("SELECT * FROM health")
    fun getAllHealthData(): Flow<List<Health>>

    @Query("UPDATE health SET heart_rate_readings = :heartRateReadings, timestamps = :timestamps WHERE user_id = :userId")
    suspend fun updateHeartRateData(userId: Long, heartRateReadings: List<Int>, timestamps: List<Date>)

    @Query("UPDATE health SET water_intake = :waterIntake WHERE user_id = :userId")
    suspend fun updateWaterIntake(userId: Long, waterIntake: Int)

    @Query("UPDATE health SET bmi = :bmi WHERE user_id = :userId")
    suspend fun updateBMI(userId: Long, bmi: Double)

    @Query("UPDATE health SET blood_glucose = :bloodGlucose WHERE user_id = :userId")
    suspend fun updateBloodGlucose(userId: Long, bloodGlucose: Double)
}