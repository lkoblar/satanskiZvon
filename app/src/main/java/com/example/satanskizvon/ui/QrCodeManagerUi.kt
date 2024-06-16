package com.example.satanskizvon.ui

import android.app.Activity.MODE_PRIVATE
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.satanskizvon.R
import com.example.satanskizvon.data.database.QRCodeDao
import com.example.satanskizvon.data.repository.QRCodeRepository
import com.example.satanskizvon.viewModel.MainViewModel
fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
}
@Composable
fun QrCOdeUi(  viewModel: MainViewModel,context: Context){
    val qr_list by viewModel.qrCodes.observeAsState(emptyList())
Column(modifier = Modifier.fillMaxHeight(0.85f)) {


LazyColumn {
    items(qr_list){item->
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .background(Color.Black)){

            Image(painter = painterResource(id = R.drawable.baseline_qr_code_24),
                contentDescription ="Edit",
                alignment = Alignment.Center,
                modifier = Modifier.padding(top = 10.dp, start = 10.dp, bottom = 10.dp)
                    .size(30.dp)
                //modifier = Modifier.weight(1f)
            )
            Text(text = "${item.id}. ",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 5.dp, top = 13.dp, bottom = 10.dp))
            Text(text = item.name,
                color = Color.White,
                fontSize = 20.sp,


                modifier = Modifier.weight(0.5f).padding(start = 5.dp,top = 13.dp, bottom = 10.dp))
            IconButton(onClick = {


         copyToClipboard(context = context,
             text = item.data)

            }) {
            Image(painter = painterResource(id = R.drawable.baseline_content_copy_24),
                contentDescription ="Edit",

                //modifier = Modifier.weight(1f)
                )

            }
            IconButton(onClick = { viewModel.deleteQRCode(item)
                val sharedPref = context.getSharedPreferences("MyApp", MODE_PRIVATE)
                val savedQRCode = sharedPref.getString("SAVED_QR_CODE", null)
                sharedPref.edit().putString("SAVED_QR_CODE", null).apply()

            }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_delete_24),
                    contentDescription = "Edit",
                    alignment = Alignment.Center,
                    modifier = Modifier.padding(top = 10.dp, end = 10.dp, bottom = 10.dp)
                        .size(35.dp)
                    //modifier = Modifier.weight(1f)
                )
            }

        }

        }

    }
}

}
@Preview(showBackground = true)
@Composable
fun ShowUi(){
    MaterialTheme {
       // QrCOdeUi()
    }
}