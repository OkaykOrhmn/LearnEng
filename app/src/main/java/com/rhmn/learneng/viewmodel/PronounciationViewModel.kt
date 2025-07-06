package com.rhmn.learneng.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.rhmn.learneng.data.AppDatabase
import com.rhmn.learneng.data.model.Pronunciation
import com.rhmn.learneng.data.model.Vocabulary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.util.Locale

class PronounciationViewModel : ViewModel() {

    private val _pronunciationList = MutableLiveData<List<Pronunciation>>()
    val pronunciationList: LiveData<List<Pronunciation>> get() = _pronunciationList

    private val _pronunciationId = MutableLiveData(0)
    val pronunciationId: LiveData<Int> get() = _pronunciationId

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> get() = _loading

    private val _recognizedText = MutableLiveData<String>()
    val recognizedText: LiveData<String> get() = _recognizedText

    private val _isListening = MutableLiveData(false)
    val isListening: LiveData<Boolean> get() = _isListening

    private var speechRecognizer: SpeechRecognizer? = null

    fun fetchPronunciationList(context: Context, dayId: Int) {
        _loading.value = true
        val db = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "my-database").build()
        viewModelScope.launch(Dispatchers.IO) {
            val list = db.pronunciationDao().getByDayId(dayId)
            withContext(Dispatchers.Main) {
                _pronunciationList.value = list
                _loading.value = false
            }
        }
    }

    fun increaseId() { _pronunciationId.value = (_pronunciationId.value ?: 0) + 1 }
    fun decreaseId() { _pronunciationId.value = (_pronunciationId.value ?: 0) - 1 }
    fun setId(id: Int) { _pronunciationId.value = id }

    fun initializeSTT(context: Context) {
        if (speechRecognizer != null) return // avoid reinit
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) { _isListening.postValue(true) }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() { _isListening.postValue(false) }
                override fun onError(error: Int) {
                    _isListening.postValue(false)
                    _recognizedText.postValue("Error: $error")
                }
                override fun onResults(results: Bundle?) {
                    _isListening.postValue(false)
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.let {
                        _recognizedText.postValue(it)
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            this@PronounciationViewModel.speechRecognizer = this
        }
    }

    fun startListening() {
        speechRecognizer?.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH))
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
    }

    fun destroySTT() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    override fun onCleared() {
        super.onCleared()
        destroySTT()
    }
}
