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
    @ColumnInfo(name = "user_id") val userId: Int, // Foreign key to User table
    @ColumnInfo(name = "heart_rate") val heartRate: Int,
    @ColumnInfo(name = "water_intake") val waterIntake: Int,
    @ColumnInfo(name = "bmi") val bmi: Double,
    @ColumnInfo(name = "blood_pressure") val bloodPressure: String,
    @ColumnInfo(name = "blood_glucose") val bloodGlucose: Double
)