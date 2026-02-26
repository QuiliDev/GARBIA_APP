package com.garbia.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class UsuarioEntity(
    @PrimaryKey val id: Int = 1,
    val nombre: String = "Usuario",
    val puntosTotales: Int = 0,
    val escaneosTotales: Int = 0,
    val co2Ahorrado: Float = 0f,
    val firebaseUid: String? = null
)
