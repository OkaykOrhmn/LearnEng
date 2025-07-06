package com.rhmn.learneng.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.rhmn.learneng.data.AppDatabase

import com.rhmn.learneng.data.model.*
import com.rhmn.learneng.services.RetrofitClient
import com.rhmn.learneng.utility.JsonDayParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val _daysList = MutableLiveData<List<Day>>()
    val daysList: LiveData<List<Day>> get() = _daysList

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean> get() = _loading

    // New LiveData for progress
    private val _downloadProgress =
        MutableLiveData<Map<Int, Int>>() // Map<DayId, ProgressPercentage>
    val downloadProgress: LiveData<Map<Int, Int>> get() = _downloadProgress

    fun fetchDaysList(context: Context) {
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "my-database"
        ).build()
        viewModelScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.api.getAllDays()
            val days = response.data
            days.forEach {
                it.apply { dayResult = DayResult.LOCK }
            }

            val dbDays = db.dayDao().getDays()
            dbDays.forEach { dbDay ->
                days.find { it.id == dbDay.id }?.apply {
                    dayResult = DayResult.OPEN
                }
            }
            withContext(Dispatchers.Main) {
                _daysList.value = days
                if (_loading.value!!) {
                    _loading.value = false
                }
            }
        }
    }

    fun fetchDayData(context: Context, dayId: Int) {
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "my-database"
        ).build()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Initialize progress
                withContext(Dispatchers.Main) {
                    _downloadProgress.value = _downloadProgress.value.orEmpty() + (dayId to 0)
                }

                val response = RetrofitClient.api.getDayById(dayId)
                val dayData = response.data.apply { dayResult = DayResult.LOCK }

                // Simulate progress updates (adjust based on actual API behavior)
                val totalSteps =
                    7 // Number of data types (Day, Vocab, Dictation, Pronunciation, GrammarQuiz, FinalQuiz, Reading, Listening)
                var currentStep = 0

                // Insert Day
                db.dayDao().insert(DbDays(id = dayId))
                currentStep++
                withContext(Dispatchers.Main) {
                    _downloadProgress.value =
                        _downloadProgress.value.orEmpty() + (dayId to (currentStep * 100 / totalSteps))
                }

                // Map and insert Vocabularies
                dayData.data?.vocabularies?.forEach { vocabJson ->
                    val word = Word(
                        sentence = vocabJson.word.sentence,
                        translation = vocabJson.word.translation
                    )
                    val examples = vocabJson.examples.map { exampleJson ->
                        Word(
                            sentence = exampleJson.sentence,
                            translation = exampleJson.translation
                        )
                    }
                    val vocabulary = Vocabulary(
                        dayId = dayId,
                        word = word,
                        examples = examples
                    )
                    db.vocabularyDao().insert(vocabulary)
                }
                currentStep++
                withContext(Dispatchers.Main) {
                    _downloadProgress.value =
                        _downloadProgress.value.orEmpty() + (dayId to (currentStep * 100 / totalSteps))
                }

                // Map and insert Dictations
                dayData.data?.dictations?.forEach { dictJson ->
                    val dictation = Dictation(
                        dayId = dayId,
                        word = Word(
                            sentence = dictJson.word.sentence,
                            translation = dictJson.word.translation
                        )
                    )
                    db.dictationDao().insert(dictation)
                }
                currentStep++
                withContext(Dispatchers.Main) {
                    _downloadProgress.value =
                        _downloadProgress.value.orEmpty() + (dayId to (currentStep * 100 / totalSteps))
                }

                // Map and insert Pronunciations
                dayData.data?.pronunciations?.forEach { pronJson ->
                    val pronunciation = Pronunciation(
                        dayId = dayId,
                        word = Word(
                            sentence = pronJson.word.sentence,
                            translation = pronJson.word.translation
                        )
                    )
                    db.pronunciationDao().insert(pronunciation)
                }
                currentStep++
                withContext(Dispatchers.Main) {
                    _downloadProgress.value =
                        _downloadProgress.value.orEmpty() + (dayId to (currentStep * 100 / totalSteps))
                }

                // Map and insert Grammar Quizzes
                dayData.data?.grammarQuiz?.forEach { quizJson ->
                    val quiz = Quiz(
                        dayId = dayId,
                        question = quizJson.question,
                        answer = quizJson.answer,
                        options = quizJson.options,
                        quizType = QuizType.GRAMMAR
                    )
                    db.quizDao().insert(quiz)
                }
                currentStep++
                withContext(Dispatchers.Main) {
                    _downloadProgress.value =
                        _downloadProgress.value.orEmpty() + (dayId to (currentStep * 100 / totalSteps))
                }

                // Map and insert Final Quizzes
                dayData.data?.finalQuiz?.forEach { quizJson ->
                    val quiz = Quiz(
                        dayId = dayId,
                        question = quizJson.question,
                        answer = quizJson.answer,
                        options = quizJson.options,
                        quizType = QuizType.FINAL
                    )
                    db.quizDao().insert(quiz)
                }
                currentStep++
                withContext(Dispatchers.Main) {
                    _downloadProgress.value =
                        _downloadProgress.value.orEmpty() + (dayId to (currentStep * 100 / totalSteps))
                }

                // Map and insert Readings with associated Quiz IDs
                dayData.data?.readings?.forEach { readingJson ->
                    val quizIds = readingJson.questions?.map { quizJson ->
                        val quiz = Quiz(
                            dayId = dayId,
                            question = quizJson.question,
                            answer = quizJson.answer,
                            options = quizJson.options,
                            quizType = QuizType.READING,
                        )
                        val newId = db.quizDao().insert(quiz)
                        newId.toInt()
                    } ?: emptyList()

                    val reading = Reading(
                        dayId = dayId,
                        title = readingJson.title,
                        text = readingJson.text,
                        translation = readingJson.translation,
                        quizIds = quizIds
                    )
                    db.readingDao().insert(reading)
                }
                currentStep++
                withContext(Dispatchers.Main) {
                    _downloadProgress.value =
                        _downloadProgress.value.orEmpty() + (dayId to (currentStep * 100 / totalSteps))
                }

                // Map and insert Listenings with associated Dialogue IDs
                dayData.data?.listening?.forEach { listeningJson ->
                    val dialogIds = listeningJson.dialogues.map { dialogJson ->
                        val dialogue = Dialogue(
                            dayId = dayId,
                            speaker = dialogJson.speaker,
                            text = dialogJson.text,
                            translation = dialogJson.translation
                        )
                        val newId = db.dialogueDao().insert(dialogue)
                        newId.toInt()
                    }

                    val listening = Listening(
                        dayId = dayId,
                        title = listeningJson.title,
                        dialogIds = dialogIds
                    )
                    db.listeningDao().insert(listening)
                }
                currentStep++
                withContext(Dispatchers.Main) {
                    _downloadProgress.value =
                        _downloadProgress.value.orEmpty() + (dayId to (currentStep * 100 / totalSteps))
                }

                // Reset progress after completion
                withContext(Dispatchers.Main) {
                    _downloadProgress.value = _downloadProgress.value.orEmpty() - dayId
                }

                // Refresh the days list
                fetchDaysList(context)
            } catch (e: Exception) {
                Log.d("TAG", "fetchDayData: $e")
                withContext(Dispatchers.Main) {
                    _downloadProgress.value = _downloadProgress.value.orEmpty() - dayId
                }
            }
        }
    }
}