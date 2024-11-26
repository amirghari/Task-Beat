package com.example.taskbeat.ui.components

import android.util.Log
import androidx.compose.runtime.toMutableStateList

/**
 * An implementation of [UiState] to be used with the Gemma model.
 */
class GemmaUiState(
    messages: List<ChatMessage> = emptyList()
) : UiState {
    private val START_TURN = "<start_of_turn>"
    private val END_TURN = "<end_of_turn>"
    private val lock = Any()

    private val _messages: MutableList<ChatMessage> = messages.toMutableStateList()
    override val messages: List<ChatMessage>
        get() = synchronized(lock) {
            _messages. apply{
                for (i in indices) {
                    this[i] = this[i].copy(
                        rawMessage = this[i].rawMessage
                            .replace(START_TURN + this[i].author + "\n", "")
                            .replace(END_TURN, "")
                    )
                }
            }.asReversed()
        }

    // Only using the last 4 messages to keep input + output short
    override val fullPrompt: String
        get() = _messages.takeLast(4).joinToString(separator = "\n") { it.rawMessage }

    override fun createLoadingMessage(): String {
        val chatMessage = ChatMessage(author = MODEL_PREFIX, isLoading = true)
        _messages.add(chatMessage)
        return chatMessage.id
    }

    fun appendFirstMessage(id: String, text: String) {
        appendMessage(id, "$START_TURN$MODEL_PREFIX\n$text", false)
    }

    override fun appendMessage(id: String, text: String, done: Boolean) {
        val index = _messages.indexOfFirst { it.id == id }
        if (index != -1) {
            val newText = if (done) {
                // Append the Suffix when model is done generating the response
                _messages[index].rawMessage + text + END_TURN
            } else {
                // Append the text
                _messages[index].rawMessage + text
            }
            _messages[index] = _messages[index].copy(rawMessage = newText, isLoading = false)
        }
    }

    override fun addMessage(text: String, author: String): String {
        val chatMessage = ChatMessage(
            rawMessage = "$START_TURN$author\n$text$END_TURN",
            author = author
        )
        _messages.add(chatMessage)
        return chatMessage.id
    }
}