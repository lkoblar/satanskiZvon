package com.example.satanskizvon.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime

import com.example.satanskizvon.data.model.Alarm
import com.example.satanskizvon.data.model.QRCode
import com.example.satanskizvon.data.repository.QRCodeRepository

class MainViewModel(private val repository: QRCodeRepository) : ViewModel()
{
    private val _alarm = MutableStateFlow<Alarm?>(null)
    val alarm: StateFlow<Alarm?> = _alarm
    private val _remainingTime = MutableStateFlow("")
    val remainingTime: StateFlow<String> = _remainingTime

    fun setAlarm(time: LocalTime)
    {
        _alarm.value = Alarm(time, true)
        updateRemainingTime(time)
    }

    private fun updateRemainingTime(alarmTime: LocalTime)
    {
        val remainingTimeDuration = calculateRemainingTime(alarmTime)
        val hours = remainingTimeDuration.toHours()
        val minutes = remainingTimeDuration.minusHours(hours).toMinutes()
        _remainingTime.value = "${hours}h ${minutes}m"

    }

    private fun calculateRemainingTime(alarmTime: LocalTime): Duration
    {
        val currentTime = LocalTime.now()
        var duration = Duration.between(currentTime, alarmTime)
        if (duration.isNegative) {
            duration = duration.plusDays(1) // Add 24 hours if the duration is negative
        }
        return duration
    }

    val qrCodes: Flow<List<QRCode>> = repository.allQRCodes

    fun addQRCode(qrCode: QRCode)
    {
        viewModelScope.launch {
            repository.insert(qrCode)
        }
    }
}
