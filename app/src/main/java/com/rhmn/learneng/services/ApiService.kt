package com.rhmn.learneng.services

import com.rhmn.learneng.data.model.Day
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("memory_bank/english_starter/getData_day.php")
    suspend fun getDayById(@Query("day") dayId: Int):DayResponse<Day>
    @GET("memory_bank/english_starter/getAllDay.php")
    suspend fun getAllDays():DayResponse<List<Day>>
}
