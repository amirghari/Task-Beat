package com.example.taskbeat.ui.viewmodels.home

import androidx.lifecycle.*
import com.example.taskbeat.data.DataRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class WaterViewModel(
    private val dataRepo: DataRepository,
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _currentUser: FirebaseUser? = auth.currentUser

    private val _currentUserId = MutableLiveData<Long?>()
    val currentUserId: LiveData<Long?> = _currentUserId

    // Observe waterIntake from the database
    val waterIntake: LiveData<Int?> = currentUserId.switchMap { userId ->
        if (userId != null) {
            dataRepo.getHealthDataByUserId(userId).asLiveData().map { health ->
                health?.waterIntake
            }
        } else {
            MutableLiveData(null)
        }
    }

    init {
        loadUserFromLocalDatabase()
    }

    fun addWaterIntake(amount: Int) {
        viewModelScope.launch {
            val userId = _currentUserId.value
            if (userId != null) {
                val existingHealthData = dataRepo.getHealthDataByUserId(userId).firstOrNull()
                if (existingHealthData != null) {
                    val newWaterIntake = (existingHealthData.waterIntake ?: 0) + amount
                    val updatedHealthData = existingHealthData.copy(waterIntake = newWaterIntake)
                    dataRepo.addOrUpdateHealthData(updatedHealthData)
                } else {
                    // If no existing health data, create a new entry
                    val newHealthData = com.example.taskbeat.model.Health(
                        userId = userId,
                        waterIntake = amount
                    )
                    dataRepo.addOrUpdateHealthData(newHealthData)
                }
            }
        }
    }

    fun removeWaterIntake(amount: Int) {
        viewModelScope.launch {
            val userId = _currentUserId.value
            if (userId != null) {
                val existingHealthData = dataRepo.getHealthDataByUserId(userId).firstOrNull()
                if (existingHealthData != null) {
                    val existingWaterIntake = existingHealthData.waterIntake ?: 0
                    val newWaterIntake = (existingWaterIntake - amount).coerceAtLeast(0)
                    val updatedHealthData = existingHealthData.copy(waterIntake = newWaterIntake)
                    dataRepo.addOrUpdateHealthData(updatedHealthData)
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