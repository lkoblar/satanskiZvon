package com.example.satanskizvon.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.satanskizvon.data.model.QRCode

@Database(entities = [QRCode::class], version = 1)
abstract class QRCodeDatabase : RoomDatabase()
{
    abstract fun qrCodeDao(): QRCodeDao

    companion object
    {
        @Volatile
        private var INSTANCE: QRCodeDatabase? = null

        fun getDatabase(context: Context): QRCodeDatabase
        {
            return INSTANCE ?: synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QRCodeDatabase::class.java,
                    "qrcode_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}