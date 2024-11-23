package com.example.taskbeat.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0L,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "password") val password: String, // Mandatory field
    @ColumnInfo(name = "age") val age: Int? = null, // Optional age field
    @ColumnInfo(name = "gender") val gender: String? = null, // Optional gender field
    @ColumnInfo(name = "display_name") val displayName: String? = null // Optional display name
)