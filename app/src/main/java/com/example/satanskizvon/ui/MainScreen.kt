package com.example.satanskizvon.ui

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.time.LocalTime
import java.util.*

import com.example.satanskizvon.viewModel.MainViewModel
import com.example.satanskizvon.data.model.Alarm

@Composable
fun MainScreen(viewModel: MainViewModel)
{
    val qrCodes by viewModel.qrCodes.collectAsState(initial = emptyList())
    val alarm by viewModel.alarm.collectAsState()
    val remainingTime by viewModel.remainingTime.collectAsState()

    Column {
        TimePicker(alarm, remainingTime, onTimeSelected = { time -> viewModel.setAlarm(time) })

        LazyColumn {
            items(qrCodes) { qrCode ->
                Text(text = qrCode.data)
            }
        }

        Button(onClick = { /* Navigate to AddQRCodeScreen */ }) {
            Text("List of QR Codes")
        }
    }
}

@Composable
fun TimePicker(alarm: Alarm?, remainingTime: String, onTimeSelected: (LocalTime) -> Unit)
{
    val context = LocalContext.current
    var timeText by remember { mutableStateOf("Select Time") }

    LaunchedEffect(alarm)
    {
        alarm?.let {
            timeText = it.time.toString()
        }
    }

    Button(onClick = {
        showTimePicker(context) { selectedTime ->
            timeText = selectedTime.toString()
            onTimeSelected(selectedTime)
        }
    }) {
        Column {
            Text(text = timeText)
            Text(text = if (remainingTime.isNotEmpty()) "Alarm will ring in $remainingTime" else "")
        }
    }
}

fun showTimePicker(context: Context, onTimeSelected: (LocalTime) -> Unit)
{
    val currentTime = Calendar.getInstance()
    val hour = currentTime.get(Calendar.HOUR_OF_DAY)
    val minute = currentTime.get(Calendar.MINUTE)

    TimePickerDialog(context, { _, selectedHour, selectedMinute ->
        val selectedTime = LocalTime.of(selectedHour, selectedMinute)
        onTimeSelected(selectedTime)
    }, hour, minute, true).show()
}
