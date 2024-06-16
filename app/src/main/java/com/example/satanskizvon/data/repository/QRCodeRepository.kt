package com.example.satanskizvon.data.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

import com.example.satanskizvon.data.database.QRCodeDao
import com.example.satanskizvon.data.model.QRCode

class QRCodeRepository(private val qrCodeDao: QRCodeDao)
{
    val allQRCodes: LiveData<List<QRCode>> = qrCodeDao.getAllQRCodes()

    suspend fun insert(qrCode: QRCode)
    {
        qrCodeDao.insertQRCode(qrCode)
    }
    suspend fun delete(qrCode: QRCode)
    {
        qrCodeDao.deleteQRCode(qrCode)
    }
}
