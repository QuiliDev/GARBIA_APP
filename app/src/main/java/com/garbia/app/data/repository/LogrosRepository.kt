package com.garbia.app.data.repository

import com.garbia.app.data.local.dao.LogroDao
import com.garbia.app.data.local.entity.LogroEntity
import com.garbia.app.data.model.Logro
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val LOGROS_PREDEFINIDOS = listOf(
    LogroEntity("primer_escaneo",  "Primer Paso",           "Realiza tu primer escaneo",               "🌱"),
    LogroEntity("cinco_escaneos",  "Explorador Verde",      "Completa 5 escaneos",                     "🔍"),
    LogroEntity("25_escaneos",     "Reciclador Activo",     "Completa 25 escaneos",                    "♻️"),
    LogroEntity("50_escaneos",     "Experto Verde",         "Completa 50 escaneos",                    "🌿"),
    LogroEntity("100_escaneos",    "Maestro del Reciclaje", "Completa 100 escaneos",                   "🏅"),
    LogroEntity("100_puntos",      "Eco Puntos Bronce",     "Acumula 100 puntos",                      "🥉"),
    LogroEntity("500_puntos",      "Eco Puntos Plata",      "Acumula 500 puntos",                      "🥈"),
    LogroEntity("1000_puntos",     "Eco Puntos Oro",        "Acumula 1000 puntos",                     "🥇"),
    LogroEntity("1kg_co2",         "Planeta Protegido",     "Ahorra 1 kg de CO₂",                     "🌍")
)

@Singleton
class LogrosRepository @Inject constructor(
    private val logroDao: LogroDao
) {
    fun getAllLogros(): Flow<List<Logro>> =
        logroDao.getAllLogros().map { list -> list.map { it.toDomain() } }

    suspend fun inicializarLogros() {
        logroDao.insertAll(LOGROS_PREDEFINIDOS)
    }

    suspend fun verificarLogros(escaneos: Int, puntos: Int, co2: Float) {
        if (escaneos >= 1)   logroDao.desbloquear("primer_escaneo")
        if (escaneos >= 5)   logroDao.desbloquear("cinco_escaneos")
        if (escaneos >= 25)  logroDao.desbloquear("25_escaneos")
        if (escaneos >= 50)  logroDao.desbloquear("50_escaneos")
        if (escaneos >= 100) logroDao.desbloquear("100_escaneos")
        if (puntos >= 100)   logroDao.desbloquear("100_puntos")
        if (puntos >= 500)   logroDao.desbloquear("500_puntos")
        if (puntos >= 1000)  logroDao.desbloquear("1000_puntos")
        if (co2 >= 1f)       logroDao.desbloquear("1kg_co2")
    }
}

private fun LogroEntity.toDomain() = Logro(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    emoji = emoji,
    desbloqueado = desbloqueado
)
