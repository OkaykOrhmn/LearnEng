package com.rhmn.learneng.data.config

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rhmn.learneng.data.model.Quiz
import com.rhmn.learneng.data.model.QuizType

@Dao
interface QuizDao {
    @Insert
    suspend fun insert(quiz: Quiz): Long

    @Query("SELECT * FROM quiz WHERE dayId = :dayId AND quizType = :quizType")
    suspend fun getByDayId(dayId: Int, quizType: QuizType): List<Quiz>

    @Query("SELECT * FROM quiz WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Int>): List<Quiz>

    @Update
    suspend fun updateQuiz(quiz: Quiz)

    @Query("UPDATE quiz SET userAnswer = NULL")
    suspend fun clearAllUserAnswers()
}