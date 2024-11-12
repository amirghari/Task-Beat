package com.example.taskbeat.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.taskbeat.data.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class BloodPressureViewModel(
    private val dataRepo: DataRepository
) : ViewModel() {

    private val _lastRecorded = MutableStateFlow<Pair<String, String>?>(null)
    val lastRecorded: StateFlow<Pair<String, String>?> = _lastRecorded

    fun saveBloodPressure(systolic: String, diastolic: String) {
        // Save logic
        _lastRecorded.value = systolic to diastolic
    }
}
