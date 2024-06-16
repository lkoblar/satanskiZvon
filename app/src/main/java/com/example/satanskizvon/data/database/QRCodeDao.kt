package com.example.satanskizvon.data.database

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import com.example.satanskizvon.data.model.QRCode

@Dao
interface QRCodeDao  {

    @Insert
    suspend fun insertQRCode(qrcode: QRCode)

    @Update
    suspend fun updateQRCode(qrcode: QRCode)

    @Delete
    suspend fun deleteQRCode(qrcode: QRCode)

    @Query("SELECT * FROM qrcode_database")
    fun getAllQRCodes() : LiveData<List<QRCode>>

}