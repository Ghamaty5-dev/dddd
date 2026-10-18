package org.example.yessoil.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/** เก็บ Mock State ของแอปไว้จุดเดียว เพื่อให้เปลี่ยนเป็น Repository จริงได้ง่าย */
class SoilViewModel(
    sensorReadings: Flow<SensorReadingUpdate>? = null,
    pointLoader: (suspend () -> List<FieldPoint>)? = null,
    private val pointCreator: (suspend (String, SoilReadings) -> FieldPoint)? = null,
) : ViewModel() {
    var readings by mutableStateOf(SoilReadings()); private set
    var isMeasuring by mutableStateOf(false); private set
    var isDeviceOnline by mutableStateOf(false); private set
    var hasSensorReading by mutableStateOf(false); private set
    var selectedCropIndex by mutableStateOf(0); private set
    var points by mutableStateOf(emptyList<FieldPoint>()); private set

    init {
        sensorReadings?.let { flow ->
            viewModelScope.launch {
                flow.collect { update ->
                    readings = update.readings
                    hasSensorReading = true
                    if (update.isNewFromDevice) {
                        isDeviceOnline = true
                        secondsSinceDeviceUpdate = 0
                        isMeasuring = false
                    }
                }
            }
            viewModelScope.launch {
                while (true) {
                    delay(1_000)
                    if (isDeviceOnline && ++secondsSinceDeviceUpdate >= DEVICE_OFFLINE_AFTER_SECONDS) {
                        isDeviceOnline = false
                        isMeasuring = false
                    }
                }
            }
        }
        pointLoader?.let { loader ->
            viewModelScope.launch {
                runCatching { loader() }.onSuccess { points = it }
            }
        }
    }

    fun startMeasurement() { isMeasuring = true }
    fun selectCrop(index: Int) { selectedCropIndex = index }
    fun addPoint(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return
        pointCreator?.let { creator ->
            viewModelScope.launch {
                runCatching { creator(trimmedName, readings) }
                    .onSuccess { points = points + it }
            }
        } ?: run {
            points = points + FieldPoint(points.size + 1, trimmedName, readings.moisture.toInt(), readings.ph, "")
        }
    }

    private var secondsSinceDeviceUpdate = DEVICE_OFFLINE_AFTER_SECONDS

    private companion object {
        const val DEVICE_OFFLINE_AFTER_SECONDS = 15
    }
}
