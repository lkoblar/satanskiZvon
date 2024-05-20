package com.example.satanskizvon.ui

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.satanskizvon.data.model.Alarm
import com.example.satanskizvon.viewModel.MainViewModel
import java.time.LocalTime
import java.util.Calendar

@Composable
fun MainScreen(viewModel: MainViewModel)
{
    val alarm by viewModel.alarm.collectAsState()
    val remainingTime by viewModel.remainingTime.collectAsState()
    val isQRCodeScanningEnabled by viewModel.isQRCodeScanningEnabled.collectAsState()
    val isVibrationEnabled by viewModel.isVibrationEnabled.collectAsState()
    val isAlarmEnabled by viewModel.isAlarmEnabled.collectAsState()

    Column(modifier = Modifier.padding(16.dp))
    {
        TimePicker(
            isAlarmEnabled,
            alarm,
            remainingTime,
            onTimeSelected = { time -> viewModel.setAlarm(time) },
            onAlarmEnabledChange = { enabled -> viewModel.setAlarmEnabled(enabled) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
        {
            Text("Turn off with QR code")
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = isQRCodeScanningEnabled,
                onCheckedChange = { viewModel.setUseQRCodeEnabled(it) }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
        {
            Text("Enable Vibration")
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = isVibrationEnabled,
                onCheckedChange = { viewModel.setVibrationEnabled(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Navigate to AddQRCodeScreen */ },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp)
        ) {
            Text("List of QR Codes")
        }
    }
}

@Composable
fun TimePicker(isAlarmEnabled: Boolean, alarm: Alarm?, remainingTime: String, onTimeSelected: (LocalTime) -> Unit, onAlarmEnabledChange: (Boolean) -> Unit)
{
    val context = LocalContext.current
    var timeText by remember { mutableStateOf("Select Time") }

    LaunchedEffect(alarm)
    {
        alarm?.let {
            timeText = it.time.toString()
        }
    }

    Button(
        onClick = {
            showTimePicker(context) { selectedTime ->
                timeText = selectedTime.toString()
                onTimeSelected(selectedTime)
            }
        },
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = ButtonDefaults.buttonElevation(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp))
        {
            Text(
                text = timeText,
                modifier = Modifier.padding(bottom = 8.dp),
                fontSize = 36.sp
            )
            if (remainingTime.isNotEmpty() && isAlarmEnabled)
                Text(text = "Alarm will ring in $remainingTime")
            else
                Text(text = "Alarm is disabled")

            Switch(
                checked = isAlarmEnabled,
                onCheckedChange = { onAlarmEnabledChange(it) }
            )
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
