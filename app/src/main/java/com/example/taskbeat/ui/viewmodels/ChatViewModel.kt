package com.example.taskbeat.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbeat.data.DataRepository
import com.example.taskbeat.data.Gemma22BModel
import com.example.taskbeat.ui.components.chat.GemmaUiState
import com.example.taskbeat.ui.components.chat.MODEL_PREFIX
import com.example.taskbeat.ui.components.chat.USER_PREFIX
import com.example.taskbeat.ui.components.chat.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ChatViewModel(
    private val dataRepo: DataRepository,
    private val model: Gemma22BModel
) : ViewModel() {
    private val _uiState: MutableStateFlow<GemmaUiState> = MutableStateFlow(GemmaUiState())
    private val _textInputEnabled: MutableStateFlow<Boolean> = MutableStateFlow(true)

    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    val isTextInputEnabled: StateFlow<Boolean> = _textInputEnabled.asStateFlow()

    private val _currentUserId = MutableStateFlow<Long?>(null)

    fun sendMessage(userMessage: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.addMessage(userMessage, USER_PREFIX)
            var currentMessageId: String? = _uiState.value.createLoadingMessage()
            setInputEnabled(false)
            try {
                val auth: FirebaseAuth = FirebaseAuth.getInstance()
                val _currentUser: FirebaseUser? = auth.currentUser
                val email = _currentUser?.email
                val user = email?.let { dataRepo.getUserByEmail(it).firstOrNull() }
                if (user != null) {
                    _currentUserId.emit(user.userId)
                    Log.d("HeartRateViewModel", "Loaded user with ID: ${user.userId}")
                } else {
                    Log.e("HeartRateViewModel", "User not found for email: $email")
                }

                val userId = _currentUserId.value
                if (userId == null) {
                    Log.e("HeartRateViewModel", "Cannot save heart rate data: userId is null")
                    return@launch
                }
                val existingHRArray = dataRepo.getHealthDataByUserId(userId).firstOrNull()?.heartRateReadings

                val fullPrompt = _uiState.value.fullPrompt
                model.generateResponseAsync(fullPrompt, existingHRArray.toString())

                model.partialResults
                    .collectIndexed { index, (partialResult, done) ->
                        currentMessageId?.let {
                            if (index == 0) {
                                _uiState.value.appendFirstMessage(it, partialResult)
                            } else {
                                _uiState.value.appendMessage(it, partialResult, done)
                            }
                            if (done) {
                                currentMessageId = null
                                setInputEnabled(true)
                            }
                        }
                    }
            } catch (e: Exception) {
                _uiState.value.addMessage(e.localizedMessage ?: "Unknown Error", MODEL_PREFIX)
                setInputEnabled(true)
            }
        }
    }

    private fun setInputEnabled(isEnabled: Boolean) {
        _textInputEnabled.value = isEnabled
    }
}