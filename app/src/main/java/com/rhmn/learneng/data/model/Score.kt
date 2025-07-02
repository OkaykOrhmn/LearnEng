package com.rhmn.learneng.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Score(
    @SerializedName("correctCount") val correctCount : Int,
    @SerializedName("wrongCount") val wrongCount : Int,
    @SerializedName("totalCount") val totalCount : Int,
    @SerializedName("resultScore") val resultScore : Int
): Serializable
