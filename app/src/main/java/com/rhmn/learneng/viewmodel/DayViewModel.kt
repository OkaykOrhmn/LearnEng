package com.rhmn.learneng.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rhmn.learneng.data.model.DayResult
import com.rhmn.learneng.data.model.DayStatus
import com.rhmn.learneng.data.model.DayStep
import com.rhmn.learneng.utility.DayStatusManager

class DayViewModel : ViewModel() {
    private val _dayStatusList = MutableLiveData<List<DayStatus>>()
    val dayStatusList: LiveData<List<DayStatus>> get() = _dayStatusList

    private var dayStatusManager: DayStatusManager? = null

    fun initialize(context: Context) {
        if (dayStatusManager == null) {
            dayStatusManager = DayStatusManager(context)
            _dayStatusList.value = dayStatusManager?.getDayStatusList() ?: emptyList()
        }
    }

    fun isDayStatusExist(dayId: Int): Boolean {
        return try {
            val day = _dayStatusList.value?.find { it.dayId == dayId }
            day != null
        } catch (_: Exception) {
            return false
        }
    }

    fun updateDayStatus(
        context: Context,
        dayId: Int,
        newDayResult1: DayResult? = null ,
        newDayResult2: DayResult? = null,
        newDayResult3: DayResult? = null
    ) {
        DayStatusManager(context).updateDayStatus(
            dayId = dayId,
            newDayResult1 = newDayResult1,
            newDayResult2 = newDayResult2,
            newDayResult3 = newDayResult3,
        )
        _dayStatusList.value = DayStatusManager(context).getDayStatusList()
    }
}