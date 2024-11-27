package com.example.taskbeat.ui.screens.chatscreen

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.taskbeat.data.Gemma22BModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LoadingChatScreen(onModelLoaded: () -> Unit) {
    val context = LocalContext.current.applicationContext
    var errorMessage by remember { mutableStateOf("") }
    var isDownloading by remember { mutableStateOf(true) }
    var isScreenVisible by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        isScreenVisible = true
        onDispose { isScreenVisible = false }
    }

    if (errorMessage.isNotEmpty()) {
        Log.e("DBG", errorMessage)
        Text("Error $errorMessage")
    } else {
        LoadingIndicator(if(isDownloading) "Downloading model" else "Loading model")
    }

    // TO DO: Fix state update, nav and crash
    LaunchedEffect(Unit, block = {
        withContext(Dispatchers.IO) {
            try {
                if (!Gemma22BModel.isOnLocalDevice(context)) {
                    Gemma22BModel.downloadModel(context)
                }
                isDownloading = false
                Gemma22BModel.getInstance(context)
                withContext(Dispatchers.Main) {
                    if (isScreenVisible) {
                        onModelLoaded()
                    }
                }
                            } catch (e: Exception) {
                Log.e("DBG", "Error in model process: $e")
                errorMessage = e.message ?: "Unknown error"
            }
        }
    })
}
