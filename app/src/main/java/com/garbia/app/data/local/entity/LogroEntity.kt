package com.garbia.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logros")
data class LogroEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val descripcion: String,
    val emoji: String,
    val desbloqueado: Boolean = false
)
