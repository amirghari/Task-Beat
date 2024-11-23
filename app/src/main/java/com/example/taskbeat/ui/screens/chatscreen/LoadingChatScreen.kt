package com.example.taskbeat.ui.screens.chatscreen

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
                    onModelLoaded()
                }
            } catch (e: Exception) {
                Log.e("DBG", "$e")
            }
        }
    })
}