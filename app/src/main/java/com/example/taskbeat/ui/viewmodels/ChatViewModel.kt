package com.example.taskbeat.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.taskbeat.data.DataRepository
import com.example.taskbeat.data.Gemma22BModel

class ChatViewModel(
    private val dataRepo: DataRepository,
    private val model: Gemma22BModel
) : ViewModel() {

}