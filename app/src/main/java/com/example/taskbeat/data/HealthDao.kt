package com.example.taskbeat.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskbeat.model.Health
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthData(health: Health)

    @Query("SELECT * FROM health WHERE user_id = :userId")
    fun getHealthDataByUserId(userId: Int): Flow<Health?>

    @Update
    suspend fun updateHealthData(health: Health)

    @Query("DELETE FROM health WHERE user_id = :userId")
    suspend fun deleteHealthDataByUserId(userId: Int)

    @Query("SELECT * FROM health")
    fun getAllHealthData(): Flow<List<Health>>
}