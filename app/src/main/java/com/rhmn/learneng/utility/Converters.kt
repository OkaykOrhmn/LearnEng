package com.rhmn.learneng.utility

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rhmn.learneng.data.model.QuizType
import com.rhmn.learneng.data.model.Word

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromWord(word: Word?): String? {
        return word?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toWord(json: String?): Word? {
        return json?.let { gson.fromJson(it, Word::class.java) }
    }

    @TypeConverter
    fun fromWordList(list: List<Word>?): String? {
        return list?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toWordList(json: String?): List<Word>? {
        return json?.let { gson.fromJson(it, object : TypeToken<List<Word>>() {}.type) }
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return list?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        return json?.let { gson.fromJson(it, object : TypeToken<List<String>>() {}.type) }
    }

    @TypeConverter
    fun fromQuizType(quizType: QuizType?): String? {
        return quizType?.name
    }

    @TypeConverter
    fun toQuizType(name: String?): QuizType? {
        return name?.let { QuizType.valueOf(it) }
    }

    @TypeConverter
    fun fromIntList(list: List<Int>?): String? {
        return list?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toIntList(json: String?): List<Int>? {
        return json?.let { gson.fromJson(it, object : TypeToken<List<Int>>() {}.type) }
    }
}