package com.example.taskbeat.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbeat.data.DataRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BloodPressureViewModel(
    private val dataRepo: DataRepository
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _currentUser: FirebaseUser? = auth.currentUser

    private val _currentUserId = MutableLiveData<Long?>()
    val currentUserId: LiveData<Long?> = _currentUserId

    private val _lastRecorded = MutableLiveData<Pair<String, String>?>()
    val lastRecorded: LiveData<Pair<String, String>?> = _lastRecorded

    init {
        loadUserFromLocalDatabase()
    }

    fun saveBloodPressure(systolic: String, diastolic: String) {
        viewModelScope.launch {
            val userId = _currentUserId.value
            if (userId != null) {
                val bloodPressureValue = "$systolic/$diastolic"
                val existingHealthData = dataRepo.getHealthDataByUserId(userId).firstOrNull()
                if (existingHealthData != null) {
                    val updatedHealthData = existingHealthData.copy(bloodPressure = bloodPressureValue)
                    dataRepo.addOrUpdateHealthData(updatedHealthData)
                } else {
                    val newHealthData = com.example.taskbeat.model.Health(
                        userId = userId,
                        bloodPressure = bloodPressureValue
                    )
                    dataRepo.addOrUpdateHealthData(newHealthData)
                }
                _lastRecorded.postValue(systolic to diastolic)
            }
        }
    }

    private fun loadUserFromLocalDatabase() {
        val email = _currentUser?.email
        if (email != null) {
            viewModelScope.launch {
                val user = dataRepo.getUserByEmail(email).firstOrNull()
                if (user != null) {
                    _currentUserId.postValue(user.userId)
                    // Load last recorded blood pressure after loading user ID
                    loadLastRecordedBloodPressure(user.userId)
                }
            }
        }
    }

    private fun loadLastRecordedBloodPressure(userId: Long) {
        viewModelScope.launch {
            val healthData = dataRepo.getHealthDataByUserId(userId).firstOrNull()
            healthData?.bloodPressure?.split("/")?.let {
                if (it.size == 2) {
                    _lastRecorded.postValue(it[0] to it[1])
                }
            }
        }
    }
}