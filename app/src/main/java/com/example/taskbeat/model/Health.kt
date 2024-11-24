package com.example.taskbeat.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "health",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Health(
    @PrimaryKey(autoGenerate = true) val healthId: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Long = 0L, // Changed from Int to Long
    @ColumnInfo(name = "heart_rate_readings") val heartRateReadings: List<Int> = emptyList(),
    @ColumnInfo(name = "water_intake") val waterIntake: Int = 0,
    @ColumnInfo(name = "bmi") val bmi: Double = 0.0,
    @ColumnInfo(name = "blood_pressure") val bloodPressure: String = "",
    @ColumnInfo(name = "blood_glucose") val bloodGlucose: Double = 0.0
)