package com.rhmn.learneng.utility

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rhmn.learneng.data.model.*
import java.io.IOException
import java.nio.charset.Charset

object JsonParser {
    private fun readJsonFromAssets(context: Context, fileName: String): String? {
        return try {
            context.assets.open(fileName).use { inputStream ->
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                String(buffer, Charset.forName("UTF-8"))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun parseDays(context: Context): List<Days>? {
        val jsonString = readJsonFromAssets(context, "combined.json") ?: return null
        return try {
            val type = object : TypeToken<List<Days>>() {}.type
            val daysList = Gson().fromJson<List<Days>>(jsonString, type)
            val dayStatusManager = DayStatusManager(context)

            daysList?.map { day ->
                day.apply {
                    dayStatus = dayStatusManager.getDayStatusById(this.id)
                        ?: DayStatus(dayId = this.id) // Add to mutable list
                }
            }


            return daysList

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getVocals(context: Context, day: Int): List<Vocal>? {
        return parseDays(context)?.getOrNull(day)?.vocabulary
    }
    fun getListening(context: Context, day: Int): Listening? {
        return parseDays(context)?.getOrNull(day)?.listening
    }

    fun getPronunciationList(context: Context, day: Int): List<Vocal>? {
        return parseDays(context)?.getOrNull(day)?.pronunciation
    }

    fun saveDayStatus(context: Context, dayStatus: DayStatus) {
        DayStatusManager(context).saveOrUpdateDayStatus(dayStatus)
    }
}