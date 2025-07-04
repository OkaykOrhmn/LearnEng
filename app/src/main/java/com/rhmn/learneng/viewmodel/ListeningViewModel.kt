package com.rhmn.learneng.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rhmn.learneng.data.model.Listening
import com.rhmn.learneng.utility.JsonParser

class ListeningViewModel : ViewModel() {
    var dayId = 0
    private val _listeningId = MutableLiveData(0)
    val listeningId: LiveData<Int> get() = _listeningId

    private val _listeningItem = MutableLiveData<Listening>()
    val listeningItem: LiveData<Listening> get() = _listeningItem

    fun fetchListeningList(context: Context) {
        val listening = JsonParser.getListening(context, dayId)
        _listeningItem.value = listening
    }

    fun increaseId() {
        _listeningId.value = (_listeningId.value ?: 0) + 1
    }

    fun decreaseId() {
        _listeningId.value = (_listeningId.value ?: 0) - 1
    }

}