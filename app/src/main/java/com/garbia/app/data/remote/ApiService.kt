package com.garbia.app.data.remote

import com.garbia.app.data.model.ResultadoEscaneo
import javax.inject.Inject
import javax.inject.Singleton

private val mockResultados = listOf(
    ResultadoEscaneo(true,  "Vidrio",   "Contenedor Verde",    "Botellas, frascos y tarros. Recuerda quitar las tapas.",             15, 0.5f),
    ResultadoEscaneo(true,  "Plástico", "Contenedor Amarillo", "Botellas de plástico, latas y briks. Aplástalos si puedes.",         10, 0.3f),
    ResultadoEscaneo(true,  "Papel",    "Contenedor Azul",     "Papel, cartón y revistas. Retira cualquier elemento de plástico.",   20, 0.4f),
    ResultadoEscaneo(true,  "Orgánico", "Contenedor Marrón",   "Restos de comida y residuos orgánicos biodegradables.",             12, 0.2f),
    ResultadoEscaneo(false)
)

@Singleton
class ApiService @Inject constructor() {

    // TODO: Reemplazar con llamada real a la API de IA (Google Vision / OpenAI)
    // Ejemplo con Ktor:
    //
    // private val client = HttpClient(Android) {
    //     install(ContentNegotiation) { json() }
    // }
    //
    // suspend fun clasificarImagen(base64: String): ResultadoEscaneo {
    //     return client.post("https://tu-api.com/classify") {
    //         setBody(ClasificacionRequest(image = base64))
    //     }.body()
    // }

    suspend fun clasificarImagen(photoUri: String): ResultadoEscaneo =
        mockResultados[kotlin.math.abs(photoUri.hashCode()) % mockResultados.size]
}
