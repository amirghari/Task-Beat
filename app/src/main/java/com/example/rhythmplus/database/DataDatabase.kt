package com.example.rhythmplus.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rhythmplus.data.HealthDao
import com.example.rhythmplus.data.UserDao
import com.example.rhythmplus.model.User
import com.example.rhythmplus.model.Health
import kotlin.concurrent.Volatile

@Database(entities = [User::class, Health::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun healthDao(): HealthDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "rhythmplus_database")
                    .fallbackToDestructiveMigration() // For handling schema changes during development
                    .build()
                    .also { Instance = it }
            }
        }
    }
}