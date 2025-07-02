package com.rhmn.learneng.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rhmn.learneng.data.model.Vocal
import com.rhmn.learneng.utility.JsonParser

class WordViewModel : ViewModel() {

    private val _vocabularyList = MutableLiveData<List<Vocal>>()
    val vocabularyList: LiveData<List<Vocal>> get() = _vocabularyList

    var dayId = 0

    private val _wordId = MutableLiveData(0)
    val wordId: LiveData<Int> get() = _wordId

    fun fetchVocabularyList(context: Context) {
        val vocab = JsonParser.getVocals(context, dayId)
        _vocabularyList.value = vocab
    }

    fun increaseId() {
        _wordId.value = (_wordId.value ?: 0) + 1
    }

    fun decreaseId() {
        _wordId.value = (_wordId.value ?: 0) - 1
    }

}