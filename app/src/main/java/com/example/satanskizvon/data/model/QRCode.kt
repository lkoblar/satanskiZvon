package com.example.satanskizvon.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "qrcode_database")
data class QRCode(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "Name") var name: String,
    @ColumnInfo(name = "Data") var data: String
)