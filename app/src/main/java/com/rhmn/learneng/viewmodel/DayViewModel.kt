package com.rhmn.learneng.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.rhmn.learneng.data.AppDatabase
import com.rhmn.learneng.data.model.DayResult
import com.rhmn.learneng.data.model.Lesson
import com.rhmn.learneng.data.model.QuizType
import com.rhmn.learneng.utility.Tools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DayViewModel : ViewModel() {
    private val _lessonItem = MutableLiveData<Lesson>()
    val lessonItem: LiveData<Lesson> get() = _lessonItem
    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean> get() = _loading

    fun fetchLesson(context: Context,dayId: Int){
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "my-database"
        ).build()
        viewModelScope.launch(Dispatchers.IO) {
            val vocabularies = db.vocabularyDao().getByDayId(dayId)
            val dictations = db.dictationDao().getByDayId(dayId)
            val pronunciations = db.pronunciationDao().getByDayId(dayId)
            val grammarQuiz = db.quizDao().getByDayId(dayId,QuizType.GRAMMAR)
            val finalQuiz = db.quizDao().getByDayId(dayId,QuizType.FINAL)
            val readings = db.readingDao().getByDayId(dayId)
            val listenings = db.listeningDao().getByDayId(dayId)
            val dialogues = db.dialogueDao().getByDayId(dayId)

            val lesson = Tools.buildLessonFromDbNullable(
                vocabularies = vocabularies,
                dictations = dictations,
                pronunciations = pronunciations,
                grammarQuiz = grammarQuiz,
                finalQuiz = finalQuiz,
                readings = readings,
                listening = listenings,
                dialogues = dialogues
            )

            // then do whatever
            withContext(Dispatchers.Main) {
                _lessonItem.value = lesson
            }

        }
    }
}