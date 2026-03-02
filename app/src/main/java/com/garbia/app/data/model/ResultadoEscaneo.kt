package com.garbia.app.data.model

data class ResultadoEscaneo(
    val identificado: Boolean,
    val tipoMaterial: String = "",
    val contenedor: String = "",
    val descripcion: String = "",
    val puntos: Int = 0,
    val co2Ahorrado: Float = 0f
)
