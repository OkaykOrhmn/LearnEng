package com.rhmn.learneng.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.rhmn.learneng.data.AppDatabase
import com.rhmn.learneng.data.model.Quiz
import com.rhmn.learneng.data.model.QuizType
import com.rhmn.learneng.data.model.Reading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizViewModel : ViewModel() {

    private val _quizList = MutableLiveData<List<Quiz>>()
    val quizList: LiveData<List<Quiz>> get() = _quizList

    private val _readingItem = MutableLiveData<Reading?>()
    val readingItem: LiveData<Reading?> get() = _readingItem

    private val _quizId = MutableLiveData(0)
    val quizId: LiveData<Int> get() = _quizId

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> get() = _loading

    private val _selectedOption = MutableLiveData<Int?>()
    val selectedOption: LiveData<Int?> get() = _selectedOption

    fun fetchQuiz(context: Context, dayId: Int, quizType: QuizType) {

        _loading.value = true
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "my-database"
        ).build()

        viewModelScope.launch(Dispatchers.IO) {
            val list = db.quizDao().getByDayId(dayId, quizType)
            var reading: Reading? = null
            if (quizType == QuizType.READING) {
                reading = db.readingDao().getByDayId(dayId).first()
            }
            withContext(Dispatchers.Main) {
                _quizList.value = list
                _loading.value = false
                _readingItem.value = reading

            }
        }

    }

    fun updateQuizField(context: Context, userAnswer: String) {
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "my-database"
        ).build()

        val currentList = _quizList.value?.toMutableList() ?: return
        val index = _quizId.value ?: return
        val updated = currentList[index].copy(userAnswer = userAnswer)
        currentList[index] = updated

        viewModelScope.launch(Dispatchers.IO) {
            db.quizDao().updateQuiz(updated)
        }
        _quizList.value = currentList
    }

    fun clearAllUserAnswers(context: Context) {
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "my-database"
        ).build()

        val clearedList = _quizList.value?.map { it.copy(userAnswer = null) }
        _quizList.value = clearedList

        viewModelScope.launch(Dispatchers.IO) {
            db.quizDao().clearAllUserAnswers()
        }
    }

    fun setId(id: Int) {
        _selectedOption.value = null
        _quizId.value = id
    }

    fun increaseId() {
        _selectedOption.value = null
        _quizId.value = (_quizId.value ?: 0) + 1
    }

    fun decreaseId() {
        _selectedOption.value = null
        _quizId.value = (_quizId.value ?: 0) - 1
    }

    fun setSelectionOption(value: Int?) {
        _selectedOption.value = value
    }
}
