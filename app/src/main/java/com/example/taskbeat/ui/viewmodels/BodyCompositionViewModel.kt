package com.example.taskbeat.ui.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.taskbeat.data.DataRepository
import com.example.taskbeat.model.Health
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BodyCompositionViewModel(
    private val dataRepo: DataRepository
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _currentUser: FirebaseUser? = auth.currentUser

    private val _currentUserId = MutableLiveData<Long?>()
    val currentUserId: LiveData<Long?> = _currentUserId

    val bmi: LiveData<Double> = currentUserId.switchMap { userId ->
        if (userId != null) {
            dataRepo.getHealthDataByUserId(userId).asLiveData().map { health ->
                health?.bmi ?: 0.0
            }
        } else {
            MutableLiveData(0.0)
        }
    }

    val weight: LiveData<Double> = currentUserId.switchMap { userId ->
        if (userId != null) {
            dataRepo.getHealthDataByUserId(userId).asLiveData().map { health ->
                health?.weight ?: 56.0
            }
        } else {
            MutableLiveData(56.0)
        }
    }

    val height: LiveData<Double> = currentUserId.switchMap { userId ->
        if (userId != null) {
            dataRepo.getHealthDataByUserId(userId).asLiveData().map { health ->
                health?.height ?: 170.0
            }
        } else {
            MutableLiveData(170.0)
        }
    }

    init {
        loadUserFromLocalDatabase()
    }

    fun updateBMI(weightValue: Double, heightValue: Double, bmiValue: Double) {
        viewModelScope.launch {
            val userId = _currentUserId.value
            if (userId != null) {
                val existingHealthData = dataRepo.getHealthDataByUserId(userId).firstOrNull()
                if (existingHealthData != null) {
                    // Update existing health data
                    val updatedHealthData = existingHealthData.copy(
                        weight = weightValue,
                        height = heightValue,
                        bmi = bmiValue
                    )
                    dataRepo.addOrUpdateHealthData(updatedHealthData)
                    Log.d("BodyCompositionVM", "BMI, weight, and height updated for userId: $userId")
                } else {
                    // Create new health data
                    val newHealthData = Health(
                        userId = userId,
                        weight = weightValue,
                        height = heightValue,
                        bmi = bmiValue
                    )
                    dataRepo.addOrUpdateHealthData(newHealthData)
                    Log.d("BodyCompositionVM", "New Health data created with BMI, weight, and height for userId: $userId")
                }
            } else {
                Log.e("BodyCompositionVM", "User ID is null. Cannot update BMI.")
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
                    Log.d("BodyCompositionVM", "User ID loaded: ${user.userId}")
                } else {
                    Log.e("BodyCompositionVM", "User not found in local database.")
                }
            }
        } else {
            Log.e("BodyCompositionVM", "Current user email is null.")
        }
    }
}