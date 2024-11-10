package com.example.taskbeat.data

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.File

class Gemma22BModel(
    context: Context
) {
    private var llmInference: LlmInference

    private val modelExists: Boolean
        get() = File(MODEL_PATH).exists()

    private val _partialResults = MutableSharedFlow<Pair<String, Boolean>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val partialResults: SharedFlow<Pair<String, Boolean>> = _partialResults.asSharedFlow()

    init {
        if (!modelExists) {
            throw IllegalArgumentException("Model not found at path: $MODEL_PATH")
        }

        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(MODEL_PATH)
            .setMaxTokens(512)
            .setResultListener { partialResult, done ->
                _partialResults.tryEmit(partialResult to done)
            }
            .build()

        llmInference = LlmInference.createFromOptions(context, options)
    }

    fun generateResponseAsync(prompt: String) {
        val gemmaPrompt = "$prompt<start_of_turn>model\n"
        llmInference.generateResponseAsync(gemmaPrompt)
    }

    companion object {
        private const val MODEL_PATH = "/data/local/tmp/llm/1/gemma2-2b-it-gpu-int8.bin"
        private var instance: Gemma22BModel? = null

        fun getInstance(context: Context): Gemma22BModel {
            return if (instance != null) {
                Log.d("DBG", "Model has already been init")
                instance!!
            } else {
                Log.d("DBG", "Model is init first time")
                Gemma22BModel(context).also { instance = it }
            }
        }
    }
}