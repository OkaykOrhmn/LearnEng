package com.rhmn.learneng.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rhmn.learneng.data.model.Days
import com.rhmn.learneng.utility.DayStatusManager
import com.rhmn.learneng.utility.JsonParser

class HomeViewModel : ViewModel() {

    private val _daysList = MutableLiveData<List<Days>>()
    val daysList: LiveData<List<Days>> get() = _daysList

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean> get() = _loading

    fun fetchDaysList(context: Context) {
        val days = JsonParser.parseDays(context)
        val daysStatus = days!!.map { it.dayStatus }
        val dayStatusManager = DayStatusManager(context)
        dayStatusManager.saveDayStatusList(daysStatus)

        _daysList.value = days
        _loading.value = false
    }
}