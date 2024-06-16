package com.example.Activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModelProvider

import com.example.satanskizvon.data.database.QRCodeDatabase
import com.example.satanskizvon.data.repository.QRCodeRepository
import com.example.satanskizvon.ui.MainScreen
import com.example.satanskizvon.viewModel.MainViewModel
import com.example.satanskizvon.viewModel.MainViewModelFactory

class MainActivity : ComponentActivity()
{
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val database = QRCodeDatabase.getDatabase(this)
        val repository = QRCodeRepository(database.qrCodeDao())
        val viewModelFactory = MainViewModelFactory(repository, this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setContent {
            MaterialTheme {
                Surface {
                    MainScreen(viewModel,this)
                }
            }
        }
    }
}