package com.example.satanskizvon


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.satanskizvon.data.repository.QRCodeRepository
import com.example.satanskizvon.viewModel.MainViewModel

class MainViewModelFactory(private val repository: QRCodeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
