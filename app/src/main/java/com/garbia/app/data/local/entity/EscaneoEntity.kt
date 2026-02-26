package com.garbia.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "escaneos")
data class EscaneoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipoMaterial: String,
    val contenedor: String,
    val puntosGanados: Int,
    val co2Ahorrado: Float,
    val fotoUri: String? = null,
    val fechaTimestamp: Long = System.currentTimeMillis()
)
