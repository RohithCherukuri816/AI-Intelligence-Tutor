package com.example.eduaituitor.data.database

import androidx.room.TypeConverter
import com.example.eduaituitor.data.QuizQuestion
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Room TypeConverters for complex data types
 * Converts between Kotlin objects and database-compatible types
 */
class Converters {
    private val gson = Gson()

    /**
     * Convert List<QuizQuestion> to JSON String for database storage
     */
    @TypeConverter
    fun fromQuizQuestionList(value: List<QuizQuestion>?): String? {
        return value?.let { gson.toJson(it) }
    }

    /**
     * Convert JSON String back to List<QuizQuestion>
     */
    @TypeConverter
    fun toQuizQuestionList(value: String?): List<QuizQuestion>? {
        return value?.let {
            val type = object : TypeToken<List<QuizQuestion>>() {}.type
            gson.fromJson(it, type)
        }
    }

    /**
     * Convert List<Int> to JSON String for database storage
     */
    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.let { gson.toJson(it) }
    }

    /**
     * Convert JSON String back to List<Int>
     */
    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return value?.let {
            val type = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(it, type)
        }
    }

    /**
     * Convert List<String> to JSON String for database storage
     */
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    /**
     * Convert JSON String back to List<String>
     */
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, type)
        }
    }
}
