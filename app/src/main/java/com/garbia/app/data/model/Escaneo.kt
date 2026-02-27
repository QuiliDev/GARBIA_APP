package com.garbia.app.data.model

data class Escaneo(
    val id: Long,
    val tipoMaterial: String,
    val contenedor: String,
    val puntosGanados: Int,
    val co2Ahorrado: Float,
    val fechaTimestamp: Long
)
