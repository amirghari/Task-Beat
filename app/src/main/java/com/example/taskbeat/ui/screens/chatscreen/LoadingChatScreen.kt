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
fun LoadingChatScreen(
    onModelLoaded: () -> Unit = { }
) {
    val context = LocalContext.current.applicationContext
    val errorMessage by remember { mutableStateOf("") }
    var isScreenVisible by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        isScreenVisible = true
        onDispose { isScreenVisible = false }
    }

    if (errorMessage != "") {
        Log.e("DBG", errorMessage)
    } else {
        LoadingIndicator()
    }

    LaunchedEffect(Unit, block = {
        withContext(Dispatchers.IO) {
            try {
                Gemma22BModel.getInstance(context)
                withContext(Dispatchers.Main) {
                    if (isScreenVisible) {
                        onModelLoaded()
                    }
                }
            } catch (e: Exception) {
                Log.e("DBG", "Error in LoadingChatScreen: $e")
            }
        }
    })
}