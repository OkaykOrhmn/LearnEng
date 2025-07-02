package com.rhmn.learneng.data.model

import com.google.gson.annotations.SerializedName

data class Days(
    @SerializedName("id") val id: Int,
    @SerializedName("vocabulary") val vocabulary: List<Vocal>,
    @SerializedName("dictation") val dictation: List<String>,
    @SerializedName("pronunciation") val pronunciation: List<Vocal>,
    @SerializedName("grammarQuiz") val grammarQuiz: List<Question>,
    @SerializedName("reading") val reading: Reading,
    @SerializedName("listening") val listening: Any,
    @SerializedName("quiz") val quiz: List<Question>,

    var dayStatus: DayStatus
)

data class Vocal(
    @SerializedName("id") val id: Int,
    @SerializedName("sentence") val word: String,
    @SerializedName("translation") val meaning: String,
    @SerializedName("examples") val examples: List<Vocal>?,

    var success: Boolean = false,
)

data class Question(
    @SerializedName("id") val id: Int,
    @SerializedName("question") val question: String,
    @SerializedName("answer") val answer: String,
    @SerializedName("options") val options: List<String>,

    val userAnswer: String?
)

data class Reading(
    @SerializedName("title") val title: String?,
    @SerializedName("text") val text: String?,
    @SerializedName("translation") val translation: String?,
    @SerializedName("questions") val questions: List<Question>,
)

data class DayStatus(
    val dayId: Int,
    var dayResult1: DayResult = DayResult.OPEN,
    var dayResult2: DayResult = DayResult.OPEN,
    var dayResult3: DayResult = DayResult.OPEN,
//    var dayStep: DayStep? = null,
//    var grammarQuizResult : List<Boolean>? = emptyList(),
//    var readingQuizResult : List<Boolean>? = emptyList(),
//    var finalQuizResult : List<Boolean>? = emptyList(),
)
