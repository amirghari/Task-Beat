package com.example.taskbeat.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.taskbeat.data.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BloodGlucoseViewModel(
    private val dataRepo: DataRepository,
) : ViewModel() {
    private val _lastRecorded = MutableStateFlow<String?>(null)
    val lastRecorded: StateFlow<String?> = _lastRecorded

    fun saveGlucoseLevel(glucoseLevel: String) {
        // Here you would save the glucose level, e.g., to a local database or repository
        _lastRecorded.value = glucoseLevel
    }

}
