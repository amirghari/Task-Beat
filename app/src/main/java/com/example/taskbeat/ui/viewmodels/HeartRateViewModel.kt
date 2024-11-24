package com.example.taskbeat.ui.viewmodels

import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.taskbeat.data.DataRepository
import com.example.taskbeat.model.Health
import com.example.taskbeat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

class HeartRateViewModel(
    private val dataRepo: DataRepository
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _currentUser: FirebaseUser? = auth.currentUser

    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId

    private val _heartRate = MutableLiveData<Int>()
    val heartRate: LiveData<Int> get() = _heartRate

    private val _isMeasuring = MutableLiveData<Boolean>()
    val isMeasuring: LiveData<Boolean> get() = _isMeasuring

    // Expose health data as LiveData
    val healthData: LiveData<Health?> = currentUserId.flatMapLatest { userId ->
        if (userId != null) {
            dataRepo.getHealthDataByUserId(userId)
        } else {
            flowOf(null)
        }
    }.asLiveData()

    // Calculate average heart rate
    val averageHeartRate: LiveData<Double> = healthData.map { health ->
        val readings = health?.heartRateReadings ?: emptyList()
        Log.d("HeartRateViewModel", "Heart Rate Readings for Average Calculation: $readings")
        val average = if (readings.isNotEmpty()) {
            readings.map { it.toDouble() }.average()
        } else {
            0.0
        }
        Log.d("HeartRateViewModel", "Calculated average heart rate: $average")
        average
    }

    private var redIntensityValues = mutableListOf<Double>()

    fun startHeartRateMeasurement() {
        _isMeasuring.value = true
        redIntensityValues.clear()
        Log.d("HeartRateViewModel", "Heart rate measurement started")

        viewModelScope.launch {
            delay(15000) // Measure for 15 seconds
            val heartRateValue = calculateHeartRateFromFrames()
            _heartRate.value = heartRateValue
            _isMeasuring.value = false
            Log.d("HeartRateViewModel", "Heart rate measurement ended with value: $heartRateValue BPM")

            saveHeartRateToDatabase(heartRateValue)
        }
    }

    fun analyzeFrame(image: ImageProxy) {
        try {
            // Process frames only when measurement is ongoing
            if (_isMeasuring.value == true) {
                val redIntensity = getRedIntensity(image)
                redIntensityValues.add(redIntensity)
                Log.d("HeartRateViewModel", "Frame analyzed, redIntensity: $redIntensity, total frames: ${redIntensityValues.size}")
            }
        } catch (e: Exception) {
            Log.e("HeartRateViewModel", "Error analyzing frame: ${e.message}")
        } finally {
            image.close()
        }
    }

    private fun getRedIntensity(image: ImageProxy): Double {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        var redSum = 0.0
        for (i in bytes.indices step 4) {
            val redValue = bytes[i].toInt() and 0xFF
            redSum += redValue
        }

        return redSum / (bytes.size / 4)
    }

    private fun calculateHeartRateFromFrames(): Int {
        Log.d("HeartRateViewModel", "Calculating heart rate from ${redIntensityValues.size} frames")
        if (redIntensityValues.isEmpty()) return 0

        val smoothedValues = redIntensityValues.windowed(5, 1) { it.average() }
        var peakCount = 0

        for (i in 1 until smoothedValues.size - 1) {
            if (smoothedValues[i] > smoothedValues[i - 1] && smoothedValues[i] > smoothedValues[i + 1]) {
                peakCount++
            }
        }

        val durationInMinutes = 15.0 / 60.0 // Measurement duration in minutes
        val beatsPerMinute = (peakCount / durationInMinutes).toInt()
        Log.d("HeartRateViewModel", "Calculated Heart Rate: $beatsPerMinute BPM")
        return beatsPerMinute
    }

    private fun saveHeartRateToDatabase(heartRateValue: Int) {
        viewModelScope.launch {
            val userId = _currentUserId.value
            if (userId == null) {
                Log.e("HeartRateViewModel", "Cannot save heart rate data: userId is null")
                return@launch
            }

            Log.d("HeartRateViewModel", "Saving heart rate data for userId: $userId")

            val existingHealthData = dataRepo.getHealthDataByUserId(userId).firstOrNull()
            Log.d("HeartRateViewModel", "Existing Health Data before update: $existingHealthData")

            val updatedHeartRates = existingHealthData?.heartRateReadings?.toMutableList() ?: mutableListOf()
            updatedHeartRates.add(heartRateValue)
            Log.d("HeartRateViewModel", "Updated Heart Rate Readings: $updatedHeartRates")

            val newHealthData = existingHealthData?.copy(
                heartRateReadings = updatedHeartRates
            ) ?: Health(
                userId = userId,
                heartRateReadings = listOf(heartRateValue),
                waterIntake = 0,
                bmi = 0.0,
                bloodPressure = "",
                bloodGlucose = 0.0
            )

            Log.d("HeartRateViewModel", "New Health Data to be saved: $newHealthData")

            dataRepo.addOrUpdateHealthData(newHealthData)
            Log.d("HeartRateViewModel", "Heart rate data saved successfully")
        }
    }

    fun loadUserFromLocalDatabase(onResult: (User?) -> Unit = {}) {
        val email = _currentUser?.email
        if (email != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val user = dataRepo.getUserByEmail(email).firstOrNull()
                if (user != null) {
                    _currentUserId.emit(user.userId)
                    Log.d("HeartRateViewModel", "Loaded user with ID: ${user.userId}")
                } else {
                    Log.e("HeartRateViewModel", "User not found for email: $email")
                }
                withContext(Dispatchers.Main) {
                    onResult(user)
                }
            }
        } else {
            Log.e("HeartRateViewModel", "Current user email is null")
            onResult(null)
        }
    }
}