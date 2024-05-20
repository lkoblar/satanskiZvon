package com.example.satanskizvon.data.model

import java.time.LocalTime

data class Alarm(
    val time: LocalTime,
    var isActive: Boolean
)

