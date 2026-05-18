package com.garbia.app.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Base64
import com.garbia.app.data.model.ResultadoEscaneo
import dagger.hilt.android.qualifiers.ApplicationContext
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

private val MOCK_RESULTADOS = listOf(
    ResultadoEscaneo(true,  "Vidrio",   "Contenedor Verde",    "Botellas, frascos y tarros. Recuerda quitar las tapas.",           250, 0.5f),
    ResultadoEscaneo(true,  "Plástico", "Contenedor Amarillo", "Botellas de plástico, latas y bricks. Aplástalos si puedes.",      300, 0.3f),
    ResultadoEscaneo(true,  "Papel",    "Contenedor Azul",     "Papel, cartón y revistas. Retira cualquier elemento de plástico.", 150, 0.4f),
    ResultadoEscaneo(true,  "Orgánico", "Contenedor Marrón",   "Restos de comida y residuos orgánables biodegradables.",           100, 0.2f),
    ResultadoEscaneo(false)
)

private const val PREFS_NAME   = "garbia_cache"
private const val KEY_RESULTADO = "ultimo_resultado"

@Singleton
class ApiService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val endpointUrl: String = "https://garbiaappbackend-production.up.railway.app/clasificar"

    private val client = HttpClient(Android) {
        install(HttpTimeout) {
            requestTimeoutMillis = 10_000
            connectTimeoutMillis =  5_000
        }
    }

    private val prefs get() = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ── Conectividad ──────────────────────────────────────────────────────────

    private fun isConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val caps = cm.getNetworkCapabilities(cm.activeNetwork ?: return false) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // ── Cache ─────────────────────────────────────────────────────────────────

    private fun guardarEnCache(resultado: ResultadoEscaneo) {
        if (!resultado.identificado) return
        val json = JSONObject().apply {
            put("tipoMaterial", resultado.tipoMaterial)
            put("contenedor",   resultado.contenedor)
            put("descripcion",  resultado.descripcion)
            put("puntos",       resultado.puntos)
            put("co2",          resultado.co2Ahorrado.toDouble())
        }.toString()
        prefs.edit().putString(KEY_RESULTADO, json).apply()
    }

    private fun cargarDesdeCache(): ResultadoEscaneo? {
        val json = prefs.getString(KEY_RESULTADO, null) ?: return null
        return try {
            val obj = JSONObject(json)
            ResultadoEscaneo(
                identificado = true,
                tipoMaterial = obj.optString("tipoMaterial"),
                contenedor   = obj.optString("contenedor"),
                descripcion  = obj.optString("descripcion"),
                puntos       = obj.optInt("puntos", 10),
                co2Ahorrado  = obj.optDouble("co2", 0.3).toFloat(),
                isFromCache  = true
            )
        } catch (e: Exception) {
            null
        }
    }

    // ── API principal ─────────────────────────────────────────────────────────

    suspend fun clasificarImagen(photoUri: String): ResultadoEscaneo {
        if (!isConnected()) {
            return cargarDesdeCache() ?: mockFallback(photoUri)
        }

        if (endpointUrl.isBlank()) {
            val resultado = mockFallback(photoUri)
            guardarEnCache(resultado)
            return resultado
        }

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

            val resultado = parsearRespuesta(respuesta) ?: mockFallback(photoUri)
            guardarEnCache(resultado)
            resultado
        } catch (e: Exception) {
            cargarDesdeCache() ?: mockFallback(photoUri)
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun leerImagenBase64(uri: String): String {
        return try {
            val parsedUri = Uri.parse(uri)
            val bytes = if (parsedUri.scheme == "file") {
                File(parsedUri.path ?: error("null path")).readBytes()
            } else {
                context.contentResolver.openInputStream(parsedUri)?.use { it.readBytes() }
                    ?: error("null stream")
            }
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            uri.takeLast(16)
        }
    }

    private fun parsearRespuesta(json: String): ResultadoEscaneo? {
        return try {
            val obj = JSONObject(json)
            if (!obj.optBoolean("identificado", false)) return ResultadoEscaneo(false)
            val material = obj.optString("material", "")
            ResultadoEscaneo(
                identificado = true,
                tipoMaterial = material,
                contenedor   = obj.optString("contenedor", ""),
                descripcion  = obj.optString("descripcion", ""),
                puntos       = puntosSegunMaterial(material),
                co2Ahorrado  = obj.optDouble("co2", 0.3).toFloat()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun puntosSegunMaterial(material: String): Int = when (material.lowercase().trim()) {
        "pilas", "baterías", "batería", "bateria", "pila"                    -> 500
        "aceite"                                                              -> 450
        "electrónico", "electronico", "raee", "residuo electrónico"          -> 400
        "metal", "lata", "aluminio"                                           -> 350
        "plástico", "plastico", "envases"                                     -> 300
        "vidrio"                                                              -> 250
        "cartón", "carton"                                                    -> 200
        "papel"                                                               -> 150
        "orgánico", "organico"                                                -> 100
        else                                                                  ->  50
    }

    private fun mockFallback(photoUri: String): ResultadoEscaneo =
        MOCK_RESULTADOS[kotlin.math.abs(photoUri.hashCode()) % MOCK_RESULTADOS.size]
}
