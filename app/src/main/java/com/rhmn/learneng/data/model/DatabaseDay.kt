package com.rhmn.learneng.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.rhmn.learneng.utility.Converters

@Entity(tableName = "day")
data class DbDays(
    @PrimaryKey()
    val id: Int = 0,
)

@Entity(tableName = "vocabularies", indices = [Index(value = ["dayId"])])
@TypeConverters(Converters::class)
data class Vocabulary(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dayId: Int,
    val word: Word,
    val examples: List<Word>
)

@Entity(tableName = "dictation", indices = [Index(value = ["dayId"])])
@TypeConverters(Converters::class)
data class Dictation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dayId: Int,
    val word: Word
)

@Entity(tableName = "pronunciation", indices = [Index(value = ["dayId"])])
@TypeConverters(Converters::class)
data class Pronunciation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dayId: Int,
    val word: Word
)

data class Word(
    val sentence: String,
    val translation: String
)

@Entity(tableName = "quiz", indices = [Index(value = ["id", "dayId"])])
@TypeConverters(Converters::class)
data class Quiz(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dayId: Int,
    val question: String,
    val answer: String,
    val options: List<String>,
    val quizType: QuizType,
    var userAnswer:String? = null
)


@Entity(tableName = "reading", indices = [Index(value = ["dayId"])])
@TypeConverters(Converters::class)
data class Reading(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dayId: Int,
    val title: String,
    val text: String,
    val translation: String,
    val quizIds: List<Int>
)

@Entity(tableName = "listening", indices = [Index(value = ["dayId"])])
@TypeConverters(Converters::class)
data class Listening(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dayId: Int,
    val title: String,
    val dialogIds: List<Int>
)

@Entity(tableName = "dialogue", indices = [Index(value = ["id", "dayId"])])
data class Dialogue(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dayId: Int,
    val speaker: String,
    val text: String,
    val translation: String
)