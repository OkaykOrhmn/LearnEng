package com.rhmn.learneng.data.model

import com.google.gson.annotations.SerializedName

data class Day(
    @SerializedName("day_id") val id: Int,
    @SerializedName("data") val data: Lesson?,
    @Transient var dayResult: DayResult = DayResult.LOCK
)

data class Lesson(
    @SerializedName("vocabularies") val vocabularies: List<VocabularyJson>? = null,
    @SerializedName("dictations") val dictations: List<DictationJson>? = null,
    @SerializedName("pronunciations") val pronunciations: List<PronunciationJson>? = null,
    @SerializedName("grammarQuiz") val grammarQuiz: List<QuizJson>? = null,
    @SerializedName("finalQuiz") val finalQuiz: List<QuizJson>? = null,
    @SerializedName("readings") val readings: List<ReadingJson>? = null, // Changed to List<ReadingJson>?
    @SerializedName("listening") val listening: List<ListeningJson>? = null
)

data class VocabularyJson(
    @SerializedName("word") val word: WordJson,
    @SerializedName("examples") val examples: List<WordJson>
)

data class DictationJson(
    @SerializedName("word") val word: WordJson
)

data class PronunciationJson(
    @SerializedName("word") val word: WordJson
)

data class WordJson(
    @SerializedName("sentence") val sentence: String,
    @SerializedName("translation") val translation: String
)

data class QuizJson(
    @SerializedName("question") val question: String,
    @SerializedName("answer") val answer: String,
    @SerializedName("options") val options: List<String>
)

data class ReadingJson(
    @SerializedName("title") val title: String,
    @SerializedName("text") val text: String,
    @SerializedName("translation") val translation: String,
    @SerializedName("questions") val questions: List<QuizJson>?
)

data class ListeningJson(
    @SerializedName("title") val title: String,
    @SerializedName("dialogues") val dialogues: List<DialogueJson>
)

data class DialogueJson(
    @SerializedName("speaker") val speaker: String,
    @SerializedName("text") val text: String,
    @SerializedName("translation") val translation: String
)
