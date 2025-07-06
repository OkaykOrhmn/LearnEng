package com.rhmn.learneng.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rhmn.learneng.data.config.*
import com.rhmn.learneng.data.model.*
import com.rhmn.learneng.utility.Converters

@Database(
    entities = [
        DbDays::class,
        Vocabulary::class,
        Dictation::class,
        Pronunciation::class,
        Quiz::class,
        Reading::class,
        Listening::class,
        Dialogue::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dayDao(): DayDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun dictationDao(): DictationDao
    abstract fun pronunciationDao(): PronunciationDao
    abstract fun quizDao(): QuizDao
    abstract fun readingDao(): ReadingDao
    abstract fun listeningDao(): ListeningDao
    abstract fun dialogueDao(): DialogueDao
}