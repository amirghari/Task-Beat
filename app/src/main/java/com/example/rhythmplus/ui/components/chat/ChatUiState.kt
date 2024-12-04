package com.example.rhythmplus.ui.components.chat

import androidx.compose.runtime.toMutableStateList

/**
 * A sample implementation of [UiState] that can be used with any model.
 */
class ChatUiState(
    messages: List<ChatMessage> = emptyList()
) : UiState {
    private val _messages: MutableList<ChatMessage> = messages.toMutableStateList()
    override val messages: List<ChatMessage> = _messages.reversed()

    // Prompt the model with the current chat history
    override val fullPrompt: String
        get() = _messages.joinToString(separator = "\n") { it.rawMessage }

    override fun createLoadingMessage(): String {
        val chatMessage = ChatMessage(author = MODEL_PREFIX, isLoading = true)
        _messages.add(chatMessage)
        return chatMessage.id
    }

    fun appendFirstMessage(id: String, text: String) {
        appendMessage(id, text, false)
    }

    override fun appendMessage(id: String, text: String, done: Boolean) {
        val index = _messages.indexOfFirst { it.id == id }
        if (index != -1) {
            val newText = _messages[index].rawMessage + text
            _messages[index] = _messages[index].copy(rawMessage = newText, isLoading = false)
        }
    }

    override fun addMessage(text: String, author: String): String {
        val chatMessage = ChatMessage(
            rawMessage = text,
            author = author
        )
        _messages.add(chatMessage)
        return chatMessage.id
    }
}