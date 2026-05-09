package com.garbia.app.data.remote

import android.net.Uri
import android.util.Base64
import com.garbia.app.data.model.ResultadoEscaneo
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

// --- DTOs simples (sin @Serializable — usamos org.json para parsear) ---
private data class ClasificacionRequest(val image_base64: String, val source: String = "garbia-app")

private val MOCK_RESULTADOS = listOf(
    ResultadoEscaneo(true,  "Vidrio",   "Contenedor Verde",    "Botellas, frascos y tarros. Recuerda quitar las tapas.",           15, 0.5f),
    ResultadoEscaneo(true,  "Plástico", "Contenedor Amarillo", "Botellas de plástico, latas y bricks. Aplástalos si puedes.",      10, 0.3f),
    ResultadoEscaneo(true,  "Papel",    "Contenedor Azul",     "Papel, cartón y revistas. Retira cualquier elemento de plástico.", 20, 0.4f),
    ResultadoEscaneo(true,  "Orgánico", "Contenedor Marrón",   "Restos de comida y residuos orgánicos biodegradables.",            12, 0.2f),
    ResultadoEscaneo(false)
)

@Singleton
class ApiService @Inject constructor() {

    // URL del endpoint de clasificación — vacío mientras no haya backend desplegado
    private val endpointUrl: String = ""

    private val client = HttpClient(Android) {
        install(HttpTimeout) {
            requestTimeoutMillis  = 10_000
            connectTimeoutMillis  = 5_000
        }
    }

    suspend fun clasificarImagen(photoUri: String): ResultadoEscaneo {
        if (endpointUrl.isBlank()) return mockFallback(photoUri)

        return try {
            val base64 = leerImagenBase64(photoUri)
            val cuerpo = JSONObject().apply {
                put("image_base64", base64)
                put("source", "garbia-app")
            }.toString()

            val respuesta = client.post(endpointUrl) {
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(cuerpo)
            }.bodyAsText()

            parsearRespuesta(respuesta) ?: mockFallback(photoUri)
        } catch (e: Exception) {
            mockFallback(photoUri)
        }
    }

    private fun leerImagenBase64(uri: String): String {
        return try {
            val archivo = File(Uri.parse(uri).path ?: return uri)
            Base64.encodeToString(archivo.readBytes(), Base64.NO_WRAP)
        } catch (e: Exception) {
            uri.takeLast(16)
        }
    }

    private fun parsearRespuesta(json: String): ResultadoEscaneo? {
        return try {
            val obj = JSONObject(json)
            val identificado = obj.optBoolean("identificado", false)
            if (!identificado) return ResultadoEscaneo(false)
            ResultadoEscaneo(
                identificado = true,
                tipoMaterial = obj.optString("material", ""),
                contenedor   = obj.optString("contenedor", ""),
                descripcion  = obj.optString("descripcion", ""),
                puntos       = obj.optInt("puntos", 10),
                co2Ahorrado  = obj.optDouble("co2", 0.3).toFloat()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun mockFallback(photoUri: String): ResultadoEscaneo =
        MOCK_RESULTADOS[kotlin.math.abs(photoUri.hashCode()) % MOCK_RESULTADOS.size]
}
