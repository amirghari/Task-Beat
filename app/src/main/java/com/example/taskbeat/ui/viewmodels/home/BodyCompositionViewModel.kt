package com.example.taskbeat.ui.viewmodels.home

import androidx.lifecycle.*
import com.example.taskbeat.data.DataRepository
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

    init {
        loadUserFromLocalDatabase()
    }

    fun updateBMI(bmiValue: Double) {
        viewModelScope.launch {
            val userId = _currentUserId.value
            if (userId != null) {
                dataRepo.updateBMI(userId, bmiValue)
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