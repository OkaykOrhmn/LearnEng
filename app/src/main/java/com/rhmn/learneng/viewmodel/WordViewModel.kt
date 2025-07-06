package com.rhmn.learneng.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.rhmn.learneng.data.AppDatabase
import com.rhmn.learneng.data.model.Vocabulary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class WordViewModel : ViewModel() {

    private val _vocabularyList = MutableLiveData<List<Vocabulary>>()
    val vocabularyList: LiveData<List<Vocabulary>> get() = _vocabularyList

    private val _wordId = MutableLiveData(0)
    val wordId: LiveData<Int> get() = _wordId

    fun fetchVocabularyList(context: Context, dayId: Int) {
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "my-database"
        ).build()
        viewModelScope.launch(Dispatchers.IO) {
            val vocabularies = db.vocabularyDao().getByDayId(dayId)

            withContext(Dispatchers.Main) {
                _vocabularyList.value = vocabularies
            }
        }
    }

    fun increaseId() {
        _wordId.value = (_wordId.value ?: 0) + 1
    }

    fun decreaseId() {
        _wordId.value = (_wordId.value ?: 0) - 1
    }

}