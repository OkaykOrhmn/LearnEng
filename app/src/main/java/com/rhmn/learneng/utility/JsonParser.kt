package com.rhmn.learneng.utility

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rhmn.learneng.data.model.*
import java.io.IOException
import java.nio.charset.Charset


class JsonDayParser// Read JSON from assets
    (private val context: Context) {

    private fun readJsonFromAssets(fileName: String): String? {
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

    // Get all days
    fun getAllDays(): List<Day>? {
        val jsonString = readJsonFromAssets("combined.json") ?: return null
        return try {
            val type = object : TypeToken<List<Day>>() {}.type
            val days = Gson().fromJson<List<Day>>(jsonString, type)
            days.forEach { it.dayResult = DayResult.LOCK }
            return days
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Get a specific day's data
    fun getDayData(dayNumber: Int): Day? {
        val days = getAllDays() ?: return null
        return days.find { it.id == dayNumber }
    }
}