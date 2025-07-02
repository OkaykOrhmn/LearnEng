package com.rhmn.learneng.utility

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rhmn.learneng.data.model.DayResult
import com.rhmn.learneng.data.model.DayStatus
import com.rhmn.learneng.data.model.DayStep


class DayStatusManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("DayStatusPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val DAY_STATUS_KEY = "DAY_STATUS_LIST"

    fun saveDayStatusList(dayStatusList: List<DayStatus>) {
        val json = gson.toJson(dayStatusList)
        prefs.edit().putString(DAY_STATUS_KEY, json).apply()
    }

    fun getDayStatusList(): List<DayStatus> {
        val json = prefs.getString(DAY_STATUS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<DayStatus>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun saveOrUpdateDayStatus(dayStatus: DayStatus) {
        val currentList = getDayStatusList().toMutableList()
        val index = currentList.indexOfFirst { it.dayId == dayStatus.dayId }
        if (index != -1) {
            currentList[index] = dayStatus
        } else {
            currentList.add(dayStatus)
        }
        saveDayStatusList(currentList)
    }

    fun getDayStatusById(dayId: Int): DayStatus? {
        if(getDayStatusList().isEmpty()){
            return DayStatus(dayId = dayId)
        }
        return getDayStatusList().find { it.dayId == dayId }
    }

    fun updateDayStatus(
        dayId: Int,
        newDayResult1: DayResult? ,
        newDayResult2: DayResult? ,
        newDayResult3: DayResult?

    ) {
        val currentDayStatus = getDayStatusById(dayId)
            ?: DayStatus(dayId = dayId)

        val updatedDayStatus = currentDayStatus.copy(
            dayResult1 = newDayResult1 ?: currentDayStatus.dayResult1,
            dayResult2 = newDayResult2 ?: currentDayStatus.dayResult2,
            dayResult3 =newDayResult3 ?: currentDayStatus.dayResult3
        )

        saveOrUpdateDayStatus(updatedDayStatus)
    }
}