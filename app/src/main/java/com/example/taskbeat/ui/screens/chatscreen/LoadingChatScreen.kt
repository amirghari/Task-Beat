package com.example.taskbeat.ui.screens.chatscreen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Button
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

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
        DeleteFileButton(context)
    } else {
        LoadingIndicator(if (isDownloading) "Downloading model" else "Loading model")
    }

    LaunchedEffect(Unit, block = {
        withContext(Dispatchers.IO) {
            try {
                Gemma22BModel.downloadModel(context)
                delay(2000)
                isDownloading = false
                delay(2000)
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

@Composable
fun DeleteFileButton(context: Context) {
    Button(onClick = {
        val targetDir = File(context.getExternalFilesDir(null), "model/llm")
        val targetFile = File(targetDir, "gemma2-2b-it-gpu-int8.bin")

        if (targetFile.exists()) {
            val isDeleted = targetFile.delete()
            val message = if (isDeleted) "File deleted successfully" else "Failed to delete file"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show()
        }
    }) {
        Text("Delete File")
    }
}
