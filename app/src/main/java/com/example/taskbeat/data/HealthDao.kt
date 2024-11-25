package com.example.taskbeat.data

import androidx.room.*
import com.example.taskbeat.model.Health
import kotlinx.coroutines.flow.Flow

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

    @Query("UPDATE health SET water_intake = :waterIntake WHERE user_id = :userId")
    suspend fun updateWaterIntake(userId: Long, waterIntake: Int)
}