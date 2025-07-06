package com.rhmn.learneng.services

import com.google.gson.annotations.SerializedName
import com.rhmn.learneng.data.model.Day

data class DayResponse<T>(
    @SerializedName("Message") val message: String,
    @SerializedName("Data") val data: T,

    )

