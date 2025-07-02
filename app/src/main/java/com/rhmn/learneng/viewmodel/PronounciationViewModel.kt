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
import com.rhmn.learneng.data.model.Vocal
import com.rhmn.learneng.utility.JsonParser
import java.util.Locale

class PronounciationViewModel : ViewModel() {
    var dayId = 0

    private val _pronunciationList = MutableLiveData<List<Vocal>>()
    val pronunciationList: LiveData<List<Vocal>> get() = _pronunciationList

    private val _pronunciationId = MutableLiveData(0)
    val pronunciationId: LiveData<Int> get() = _pronunciationId

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> get() = _loading

    fun updatePronunciationField(newSuccess: Boolean) {
        val currentList = _pronunciationList.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.id == _pronunciationId.value }
        if (index != -1) {
            val updated = currentList[index].copy(success = newSuccess)
            currentList[index] = updated
            _pronunciationList.value = currentList
        }
    }

    fun fetchPronunciationList(context: Context) {
        val pronunciation = JsonParser.getPronunciationList(context, dayId)
        _loading.value = false
        _pronunciationList.value = pronunciation
    }

    fun increaseId() {
        _pronunciationId.value = (_pronunciationId.value ?: 0) + 1
    }

    fun decreaseId() {
        _pronunciationId.value = (_pronunciationId.value ?: 0) - 1
    }

    fun setId(id: Int) {
        _pronunciationId.value = id
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private val _recognizedText = MutableLiveData<String>()
    val recognizedText: LiveData<String> = _recognizedText
    private val _isListening = MutableLiveData<Boolean>()
    val isListening: LiveData<Boolean> = _isListening

    fun initializeSTT(context: Context) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _isListening.value = false
            }

            override fun onError(error: Int) {
                _isListening.value = false
                _recognizedText.value = "Error: $error"
            }

            override fun onResults(results: Bundle?) {
                _isListening.value = false
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let {
                    if (it.isNotEmpty()) {
                        _recognizedText.value = it[0]
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
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