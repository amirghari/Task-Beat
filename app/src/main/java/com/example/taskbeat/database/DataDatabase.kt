package com.example.taskbeat.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.taskbeat.database.Converters
import com.example.taskbeat.data.HealthDao
import com.example.taskbeat.data.UserDao
import com.example.taskbeat.model.User
import com.example.taskbeat.model.Health
import kotlin.concurrent.Volatile

@Database(entities = [User::class, Health::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun healthDao(): HealthDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "taskbeat_database")
                    .fallbackToDestructiveMigration() // For handling schema changes during development
                    .build()
                    .also { Instance = it }
            }
        }
    }
}