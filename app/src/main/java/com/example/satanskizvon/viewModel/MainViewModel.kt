package com.example.satanskizvon.viewModel

import android.app.AlarmManager
import android.app.PendingIntent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.content.Context
import android.content.Intent
import java.time.Duration
import java.time.LocalTime
import java.time.LocalDate
import java.time.ZoneId

import com.example.satanskizvon.AlarmReceiver
import com.example.satanskizvon.data.model.Alarm
import com.example.satanskizvon.data.model.QRCode
import com.example.satanskizvon.data.repository.QRCodeRepository


class MainViewModel(private val repository: QRCodeRepository, private val context: Context) : ViewModel()
{
    private val _alarm = MutableStateFlow<Alarm?>(null)
    val alarm: StateFlow<Alarm?> = _alarm

    private val _remainingTime = MutableStateFlow("")
    val remainingTime: StateFlow<String> = _remainingTime

    private val _isVibrationEnabled = MutableStateFlow(false)
    val isVibrationEnabled: StateFlow<Boolean> = _isVibrationEnabled

    private val _isQRCodeScanningEnabled = MutableStateFlow(false)
    val isQRCodeScanningEnabled: StateFlow<Boolean> = _isQRCodeScanningEnabled

    private val _isAlarmEnabled = MutableStateFlow(false)
    val isAlarmEnabled: StateFlow<Boolean> = _isAlarmEnabled

    init
    {
        loadPreferences()
    }

    fun setAlarm(time: LocalTime)
    {
        _alarm.value = Alarm(time, true)
        updateRemainingTime(time)
        savePreference("alarm_set_time", time.toString())

        if (_isAlarmEnabled.value)
            setAlarmManager(time)
    }

    private fun setAlarmManager(time: LocalTime)
    {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val triggerTime = time.atDate(LocalDate.now())
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }

    fun setAlarmEnabled(enabled: Boolean)
    {
        _isAlarmEnabled.value = enabled
        savePreference("alarm_enabled", enabled)

        if (!enabled)
            stopAndCancelAlarm()
        else
        {
            _alarm.value?.let { alarm ->
                setAlarmManager(alarm.time)
            }
        }
    }

    private fun stopAndCancelAlarm()
    {
        // Cancel future alarms
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)

        // Stop if ringing
        val stopIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.example.satanskizvon.ACTION_STOP_ALARM"
        }
        context.sendBroadcast(stopIntent)
    }

    fun setVibrationEnabled(enabled: Boolean)
    {
        _isVibrationEnabled.value = enabled
        savePreference("vibration_enabled", enabled)
    }

    fun setUseQRCodeEnabled(enabled: Boolean)
    {
        _isQRCodeScanningEnabled.value = enabled
        savePreference("useQRCode_enabled", enabled)
    }

    private fun savePreference(key: String, value: Any)
    {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
            with(prefs.edit())
            {
                when (value)
                {
                    is Boolean -> putBoolean(key, value)
                    is String -> putString(key, value)
                    else -> throw IllegalArgumentException("Invalid type for preference value")
                }
                apply()
            }
        }
    }

    private fun loadPreferences()
    {
        val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        _isAlarmEnabled.value = prefs.getBoolean("alarm_enabled", false)
        _isVibrationEnabled.value = prefs.getBoolean("vibration_enabled", false)
        _isQRCodeScanningEnabled.value = prefs.getBoolean("useQRCode_enabled", false)

        val alarmTimeString = prefs.getString("alarm_set_time", null)
        if (alarmTimeString != null)
        {
            val alarmTime = LocalTime.parse(alarmTimeString)
            _alarm.value = Alarm(alarmTime, true)
            updateRemainingTime(alarmTime)
        }
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
        if (duration.isNegative)
        {
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
