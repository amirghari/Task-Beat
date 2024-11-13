package com.example.taskbeat.ui.viewmodels

import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbeat.data.DataRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

class HeartRateViewModel(
    private val dataRepo: DataRepository
) : ViewModel() {

    private val _heartRate = MutableLiveData<Int>()
    val heartRate: LiveData<Int> get() = _heartRate

    private val _isMeasuring = MutableLiveData<Boolean>()
    val isMeasuring: LiveData<Boolean> get() = _isMeasuring

    private var redIntensityValues = mutableListOf<Double>()

    fun startHeartRateMeasurement() {
        _isMeasuring.value = true
        redIntensityValues.clear()

        viewModelScope.launch {
            delay(15000) // Delay for measurement process to gather enough data
            _heartRate.value = calculateHeartRateFromFrames()
            _isMeasuring.value = false
        }
    }

    fun analyzeFrame(image: ImageProxy) {
        val redIntensity = getRedIntensity(image)
        redIntensityValues.add(redIntensity)
        image.close()
    }

    private fun getRedIntensity(image: ImageProxy): Double {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)

        var redSum = 0.0
        for (i in bytes.indices step 4) {
            // Extracting the red channel value
            val redValue = bytes[i].toInt() and 0xFF
            redSum += redValue
        }

        return redSum / (bytes.size / 4)
    }

    private fun calculateHeartRateFromFrames(): Int {
        if (redIntensityValues.isEmpty()) return 0

        val smoothedValues = redIntensityValues.windowed(5, 1) { it.average() }
        var peakCount = 0

        for (i in 1 until smoothedValues.size - 1) {
            if (smoothedValues[i] > smoothedValues[i - 1] && smoothedValues[i] > smoothedValues[i + 1]) {
                peakCount++
            }
        }

        val beatsPerMinute = (peakCount * 4)
        return beatsPerMinute
    }
}
