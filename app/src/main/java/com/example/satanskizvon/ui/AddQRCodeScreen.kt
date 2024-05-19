package com.example.satanskizvon.ui
/*

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import com.example.satanskizvon.data.model.QRCode
import com.example.satanskizvon.viewModel.AlarmViewModel

@Composable
fun AddQRCodeScreen(viewModel: AlarmViewModel = viewModel()) {
    var qrCodeData by remember { mutableStateOf("") }

    Column {
        TextField(value = qrCodeData, onValueChange = { qrCodeData = it })
        Button(onClick = {
            if (qrCodeData.isNotBlank()) {
                qrCodeData = ""
                viewModel.addQRCode(QRCode(data = qrCodeData))
            }
        }) {
            Text("Add")
        }
    }
}

 */