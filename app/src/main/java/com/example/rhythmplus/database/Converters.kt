package com.example.rhythmplus.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {

    @TypeConverter
    fun fromDateList(dates: List<Date>?): String? {
        if (dates == null) return null
        val gson = Gson()
        return gson.toJson(dates)
    }

    @TypeConverter
    fun toDateList(dateString: String?): List<Date>? {
        if (dateString == null) return emptyList()
        val listType = object : TypeToken<List<Date>>() {}.type
        return Gson().fromJson(dateString, listType)
    }

    @TypeConverter
    fun fromIntList(value: List<Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, listType)
    }
}