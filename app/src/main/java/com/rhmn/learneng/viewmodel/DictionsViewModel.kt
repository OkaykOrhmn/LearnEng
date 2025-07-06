package com.rhmn.learneng.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.rhmn.learneng.data.AppDatabase
import com.rhmn.learneng.data.model.Dictation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DictionsViewModel : ViewModel() {

    private val _dictionList = MutableLiveData<List<Dictation>>()
    val dictionList: LiveData<List<Dictation>> get() = _dictionList

    private val _dictionId = MutableLiveData(0)
    val dictionId: LiveData<Int> get() = _dictionId

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> get() = _loading

    private val _selectedDictionLetters = MutableLiveData<List<String>>(emptyList())
    val selectedDictionLetters: LiveData<List<String>> get() = _selectedDictionLetters

    private val _unselectedDictionLetters = MutableLiveData<List<String>>(emptyList())
    val unselectedDictionLetters: LiveData<List<String>> get() = _unselectedDictionLetters

    fun fetchDictionList(context: Context, dayId: Int) {
        _loading.value = true
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "my-database"
        ).build()

        viewModelScope.launch(Dispatchers.IO) {
            val list = db.dictationDao().getByDayId(dayId)
            withContext(Dispatchers.Main) {
                _dictionList.value = list
                _loading.value = false
            }
        }
    }

    fun initializeLetterLists() {
        clearLetterLists()
        val currentDiction = _dictionList.value?.getOrNull(_dictionId.value ?: 0) ?: return
        val letters = currentDiction.word.sentence.map { it.toString() }.shuffled()
        _unselectedDictionLetters.value = letters
    }

    private fun clearLetterLists() {
        _selectedDictionLetters.value = emptyList()
        _unselectedDictionLetters.value = emptyList()
    }

    fun setId(id: Int) {
        _dictionId.value = id
    }

    fun increaseId() {
        _dictionId.value = (_dictionId.value ?: 0) + 1
    }

    fun decreaseId() {
        _dictionId.value = (_dictionId.value ?: 0) - 1
    }

    fun addToSelectedLetters(item: String) {
        val updated = _selectedDictionLetters.value?.toMutableList() ?: mutableListOf()
        updated.add(item)
        _selectedDictionLetters.value = updated
    }

    fun removeFromSelectedLetters(item: String) {
        val updated = _selectedDictionLetters.value?.toMutableList() ?: return
        updated.remove(item)
        _selectedDictionLetters.value = updated
    }

    fun addToUnselectedLetters(item: String) {
        val updated = _unselectedDictionLetters.value?.toMutableList() ?: mutableListOf()
        updated.add(item)
        _unselectedDictionLetters.value = updated
    }

    fun removeFromUnselectedLetters(item: String) {
        val updated = _unselectedDictionLetters.value?.toMutableList() ?: return
        updated.remove(item)
        _unselectedDictionLetters.value = updated
    }
}
