package com.example.rhythmplus.ui.viewmodels

import androidx.lifecycle.*
import com.example.rhythmplus.data.DataRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BloodGlucoseViewModel(
    private val dataRepo: DataRepository,
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _currentUser: FirebaseUser? = auth.currentUser

    private val _currentUserId = MutableLiveData<Long?>()
    val currentUserId: LiveData<Long?> = _currentUserId

    // Observe last recorded blood glucose from the repository
    val lastRecorded: LiveData<String?> = currentUserId.switchMap { userId ->
        if (userId != null) {
            dataRepo.getHealthDataByUserId(userId).asLiveData().map { health ->
                health?.bloodGlucose?.toString()
            }
        } else {
            MutableLiveData(null)
        }
    }

    init {
        loadUserFromLocalDatabase()
    }

    fun saveGlucoseLevel(glucoseLevel: String) {
        viewModelScope.launch {
            val userId = _currentUserId.value
            if (userId != null) {
                val glucoseValue = glucoseLevel.toDoubleOrNull()
                if (glucoseValue != null) {
                    val existingHealthData = dataRepo.getHealthDataByUserId(userId).firstOrNull()
                    if (existingHealthData != null) {
                        // Update the existing health data with the new glucose level
                        val updatedHealthData = existingHealthData.copy(bloodGlucose = glucoseValue)
                        dataRepo.addOrUpdateHealthData(updatedHealthData)
                    } else {
                        // Create new health data entry if it doesn't exist
                        val newHealthData = com.example.rhythmplus.model.Health(
                            userId = userId,
                            bloodGlucose = glucoseValue
                        )
                        dataRepo.addOrUpdateHealthData(newHealthData)
                    }
                }
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
                }
            }
        }
    }
}