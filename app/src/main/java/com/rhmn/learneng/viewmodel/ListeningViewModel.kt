package com.rhmn.learneng.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.rhmn.learneng.data.AppDatabase
import com.rhmn.learneng.data.model.Dialogue
import com.rhmn.learneng.data.model.Listening
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListeningViewModel : ViewModel() {

    private val _listeningItem = MutableLiveData<Listening>()
    val listeningItem: LiveData<Listening> get() = _listeningItem

    private val _dialogItem = MutableLiveData<List<Dialogue>>()
    val dialogItem: LiveData<List<Dialogue>> get() = _dialogItem

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> get() = _loading


    fun fetchListeningList(context: Context, dayId: Int) {
        _loading.value = true
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "my-database"
        ).build()

        viewModelScope.launch(Dispatchers.IO) {
            val listening = db.listeningDao().getByDayId(dayId)
            val dialogue = db.dialogueDao().getByIds(listening.first().dialogIds)
            withContext(Dispatchers.Main) {
                _listeningItem.value = listening.first()
                _dialogItem.value = dialogue
                _loading.value = false
            }
        }
    }


}