package com.example.taskbeat.data

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
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
            .setMaxTokens(768)
            .setResultListener { partialResult, done ->
                _partialResults.tryEmit(partialResult to done)
            }
            .build()

        llmInference = LlmInference.createFromOptions(context, options)
    }

    private val systemEnforcement = "<start_of_turn>system\nYou are an AI called HealthBot resides within a mobile application called TaskBeat that provide health related tips and instructions<end_of_turn>\n"
    private val userEnforcement = ". Answer with moderate length. If the requests are unrelated to health, politely redirect the conversation to health topics. Always respond in a professional and health-focused manner."

    fun generateResponseAsync(prompt: String) {
        val insertionPoint = prompt.indexOf("<end_of_turn>")
        val modifiedUserPrompt = StringBuilder(prompt)
            .insert(insertionPoint, userEnforcement)
            .toString()
        val modifiedMessage = "$systemEnforcement$modifiedUserPrompt\n<start_of_turn>model\n"
        llmInference.generateResponseAsync(modifiedMessage)
    }

    companion object {
        private const val MODEL_PATH = "/data/local/tmp/llm/1/gemma2-2b-it-gpu-int8.bin"
        private var instance: Gemma22BModel? = null

        fun getInstance(context: Context): Gemma22BModel {
            return if (instance != null) {
                instance!!
            } else {
                Gemma22BModel(context).also { instance = it }
            }
        }
    }
}