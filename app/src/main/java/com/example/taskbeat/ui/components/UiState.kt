package com.example.taskbeat.ui.components

interface UiState {
    val messages: List<ChatMessage>
    val fullPrompt: String

    /**
     * Creates a new loading message.
     * Returns the id of that message.
     */
    fun createLoadingMessage(): String

    /**
     * Appends the specified text to the message with the specified ID.
     * @param done - indicates whether the model has finished generating the message.
     */
    fun appendMessage(id: String, text: String, done: Boolean = false)

    /**
     * Creates a new message with the specified text and author.
     * Return the id of that message.
     */
    fun addMessage(text: String, author: String): String
}