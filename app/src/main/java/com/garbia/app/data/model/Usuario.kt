package com.garbia.app.data.model

data class Usuario(
    val id: Int = 1,
    val nombre: String,
    val puntosTotales: Int,
    val escaneosTotales: Int,
    val co2Ahorrado: Float,
    val nivel: Int,
    val nivelLabel: String,
    val puntosEnNivel: Int,
    val puntosParaSiguienteNivel: Int
)
