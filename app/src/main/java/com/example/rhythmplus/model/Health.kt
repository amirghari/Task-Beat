package com.example.rhythmplus.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

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
    @ColumnInfo(name = "timestamps") val timestamps: List<Date> = emptyList(), // Track the time of each reading
    @ColumnInfo(name = "water_intake") val waterIntake: Int = 0,
    @ColumnInfo(name = "bmi") val bmi: Double = 0.0,
    @ColumnInfo(name = "weight") val weight: Double = 56.0,
    @ColumnInfo(name = "height") val height: Double = 170.0,
    @ColumnInfo(name = "blood_pressure") val bloodPressure: String = "",
    @ColumnInfo(name = "blood_glucose") val bloodGlucose: Double = 0.0
)