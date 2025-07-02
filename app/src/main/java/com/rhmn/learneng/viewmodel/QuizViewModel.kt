package com.rhmn.learneng.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rhmn.learneng.data.model.Question
import com.rhmn.learneng.data.model.QuizType
import com.rhmn.learneng.data.model.Reading
import com.rhmn.learneng.utility.JsonParser

class QuizViewModel : ViewModel() {
    var dayId = 0

    private val _quizList = MutableLiveData<List<Question>>()
    val quizList: LiveData<List<Question>> get() = _quizList

    private val _readingItem = MutableLiveData<Reading?>()
    val readingItem: LiveData<Reading?> get() = _readingItem

    private val _quizId = MutableLiveData(0)
    val quizId: LiveData<Int> get() = _quizId

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> get() = _loading

    private val _selectedOption = MutableLiveData<Int?>()
    val selectedOption: LiveData<Int?> get() = _selectedOption

    fun fetchReading(context: Context, quizType: QuizType) {
        val days = JsonParser.parseDays(context)!![dayId]
        when (quizType) {
            QuizType.READING -> {
                _readingItem.value = days.reading
                _quizList.value = days.reading.questions
            }

            QuizType.GRAMMAR -> {
                _readingItem.value = null
                _quizList.value = days.grammarQuiz
            }

            QuizType.FINAL -> {
                _readingItem.value = null
                _quizList.value = days.quiz
            }
        }
        _loading.value = false
    }

    fun updateQuizField(userAnswer: String) {
        val currentList = _quizList.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.id == _quizId.value }
        if (index != -1) {
            val updated = currentList[index].copy(userAnswer = userAnswer)
            currentList[index] = updated
            _quizList.value = currentList
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
