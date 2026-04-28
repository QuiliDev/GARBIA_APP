package com.garbia.app.data.repository

import com.garbia.app.data.local.dao.RachaDao
import com.garbia.app.data.local.entity.RachaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class Racha(val actual: Int, val maxima: Int)

@Singleton
class RachaRepository @Inject constructor(
    private val rachaDao: RachaDao
) {
    fun getRacha(): Flow<Racha> =
        rachaDao.getRacha().map { it?.toDomain() ?: Racha(0, 0) }

    suspend fun registrarEscaneoHoy() {
        val ahora     = System.currentTimeMillis()
        val hoyInicio = inicioDelDia(ahora)
        val ayerInicio = hoyInicio - 86_400_000L

        val racha = rachaDao.getRachaOnce() ?: RachaEntity()

        val nuevaRacha = when {
            // Ya escaneó hoy → no cambia la racha
            racha.ultimoEscaneoTimestamp >= hoyInicio -> racha

            // Escaneó ayer → incrementa racha
            racha.ultimoEscaneoTimestamp >= ayerInicio -> {
                val nueva = racha.rachaActual + 1
                racha.copy(
                    rachaActual             = nueva,
                    rachaMáxima             = maxOf(nueva, racha.rachaMáxima),
                    ultimoEscaneoTimestamp  = ahora
                )
            }

            // No escaneó ayer → resetea racha a 1
            else -> racha.copy(
                rachaActual             = 1,
                rachaMáxima             = maxOf(1, racha.rachaMáxima),
                ultimoEscaneoTimestamp  = ahora
            )
        }

        rachaDao.upsert(nuevaRacha)
    }

    private fun inicioDelDia(timestamp: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}

private fun RachaEntity.toDomain() = Racha(rachaActual, rachaMáxima)
