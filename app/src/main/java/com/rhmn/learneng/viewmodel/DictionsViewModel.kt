package com.rhmn.learneng.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rhmn.learneng.utility.JsonParser

class DictionsViewModel : ViewModel() {
    var dayId = 0

    private val _dictionList = MutableLiveData<List<Triple<Int, String, Boolean>>>()
    val dictionList: LiveData<List<Triple<Int, String, Boolean>>> get() = _dictionList

    private val _dictionId = MutableLiveData<Int>(0)
    val dictionId: LiveData<Int> get() = _dictionId

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean> get() = _loading

    private val _selectedDictionLetterList = MutableLiveData<List<String>>()
    val selectedDictionLetterList: LiveData<List<String>> get() = _selectedDictionLetterList
    private val _unselectedDictionLetterList = MutableLiveData<List<String>>()
    val unselectedDictionLetterList: LiveData<List<String>> get() = _unselectedDictionLetterList

    fun updateDictionField(newSuccess: Boolean) {
        val currentList = _dictionList.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.first == _dictionId.value }
        if (index != -1) {
            val updated = currentList[index].copy(third = newSuccess)
            currentList[index] = updated
            _dictionList.value = currentList
        }
    }

    fun fetchDictionList(context: Context) {
        val diction = JsonParser.parseDays(context)!![dayId].dictation
        _dictionList.value = diction.mapIndexed { index, it ->
            Triple(index, it, false)
        }
        _loading.value = false
    }

    fun fillUnselectedDictionLetterList() {
        cleanList()
        val diction = _dictionList.value?.get(_dictionId.value!!)
        val dictionLetters = diction?.second!!.map { it.toString() }
        val current: ArrayList<String> = ArrayList();
        current.addAll(dictionLetters)
        if (diction.third) {
            _selectedDictionLetterList.value = current
        } else {
            current.shuffle()
            _unselectedDictionLetterList.value = current
        }
    }

    private fun cleanList() {
        _selectedDictionLetterList.value = emptyList()
        _unselectedDictionLetterList.value = emptyList()
    }

    fun setId(id: Int) {
        _dictionId.value = id
    }

    fun increaseId() {
        _dictionId.value = _dictionId.value?.plus(1)
    }

    fun decreaseId() {
        _dictionId.value = _dictionId.value?.minus(1)
    }

    fun addItemToSelectedDictionLetterList(item: String) {
        val currentList = _selectedDictionLetterList.value?.toMutableList() ?: mutableListOf()
        currentList.add(item)
        _selectedDictionLetterList.value = currentList
    }

    fun removeItemFromSelectedDictionLetterList(item: String) {
        val currentList = _selectedDictionLetterList.value?.toMutableList() ?: return
        currentList.remove(item)
        _selectedDictionLetterList.value = currentList
    }

    fun addItemToUnelectedDictionLetterList(item: String) {
        val currentList = _unselectedDictionLetterList.value?.toMutableList() ?: mutableListOf()
        currentList.add(item)
        _unselectedDictionLetterList.value = currentList
    }

    fun removeItemFromUnselectedDictionLetterList(item: String) {
        val currentList = _unselectedDictionLetterList.value?.toMutableList() ?: return
        currentList.remove(item)
        _unselectedDictionLetterList.value = currentList
    }
}