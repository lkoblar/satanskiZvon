package com.example.satanskizvon.data.repository

import kotlinx.coroutines.flow.Flow

import com.example.satanskizvon.data.database.QRCodeDao
import com.example.satanskizvon.data.model.QRCode

class QRCodeRepository(private val qrCodeDao: QRCodeDao)
{
    val allQRCodes: Flow<List<QRCode>> = qrCodeDao.getAllQRCodes()

    suspend fun insert(qrCode: QRCode)
    {
        qrCodeDao.insertQRCode(qrCode)
    }
}
