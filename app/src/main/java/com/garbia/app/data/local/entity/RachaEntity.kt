package com.garbia.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "racha")
data class RachaEntity(
    @PrimaryKey val id: Int = 1,
    val rachaActual: Int = 0,
    val rachaMáxima: Int = 0,
    val ultimoEscaneoTimestamp: Long = 0L
)
