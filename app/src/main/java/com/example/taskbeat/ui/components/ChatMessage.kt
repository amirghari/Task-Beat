package com.example.taskbeat.ui.components

import java.util.UUID

const val USER_PREFIX = "user"
const val MODEL_PREFIX = "model"

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val rawMessage: String = "",
    val author: String,
    val isLoading: Boolean = false
) {
    val isFromUser: Boolean
        get() = author == USER_PREFIX
    val message: String
        get() = rawMessage.trim()
}