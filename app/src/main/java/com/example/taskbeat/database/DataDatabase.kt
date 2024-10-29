package com.example.taskbeat.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.taskbeat.data.DataDao
import com.example.taskbeat.model.ParliamentMember
import com.example.taskbeat.model.ParliamentMemberExtra
import com.example.taskbeat.model.ParliamentMemberLocal
import kotlin.concurrent.Volatile

@Database(entities = [ParliamentMember::class, ParliamentMemberExtra::class, ParliamentMemberLocal::class], version = 1, exportSchema = false)
abstract class DataDatabase : RoomDatabase() {
    abstract fun dataDao(): DataDao

    companion object {
        @Volatile
        private var Instance: DataDatabase? = null

        fun getDatabase(context: Context): DataDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, DataDatabase::class.java, "parliament_members_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}