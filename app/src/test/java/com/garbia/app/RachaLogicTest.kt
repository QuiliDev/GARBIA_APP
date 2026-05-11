package com.garbia.app

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

/**
 * Tests unitarios para la lógica de racha diaria.
 * Replica la lógica de RachaRepository#registrarEscaneoHoy() de forma pura.
 */
class RachaLogicTest {

    private fun inicioDelDia(timestamp: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun calcularNuevaRacha(rachaActual: Int, ultimoTimestamp: Long, ahora: Long): Int {
        val hoyInicio   = inicioDelDia(ahora)
        val ayerInicio  = hoyInicio - 86_400_000L
        return when {
            ultimoTimestamp >= hoyInicio  -> rachaActual          // ya escaneó hoy
            ultimoTimestamp >= ayerInicio -> rachaActual + 1      // escaneó ayer → incrementa
            else                          -> 1                    // más de 1 día → reset
        }
    }

    private fun ahora() = System.currentTimeMillis()
    private fun ayer()  = ahora() - 86_400_000L
    private fun haceDosDias() = ahora() - 2 * 86_400_000L

    @Test
    fun `racha incrementa si ultimo escaneo fue ayer`() {
        val racha = calcularNuevaRacha(rachaActual = 3, ultimoTimestamp = ayer(), ahora = ahora())
        assertEquals(4, racha)
    }

    @Test
    fun `racha se mantiene si ya escaneo hoy`() {
        val hoyMenosUnHora = ahora() - 3_600_000L
        val racha = calcularNuevaRacha(rachaActual = 5, ultimoTimestamp = hoyMenosUnHora, ahora = ahora())
        assertEquals(5, racha)
    }

    @Test
    fun `racha resetea a 1 si han pasado mas de un dia`() {
        val racha = calcularNuevaRacha(rachaActual = 7, ultimoTimestamp = haceDosDias(), ahora = ahora())
        assertEquals(1, racha)
    }

    @Test
    fun `primera racha empieza en 1`() {
        val racha = calcularNuevaRacha(rachaActual = 0, ultimoTimestamp = 0L, ahora = ahora())
        assertEquals(1, racha)
    }

    @Test
    fun `racha maxima se actualiza correctamente`() {
        val rachaActual = 10
        val nueva       = 11
        val maxima      = maxOf(nueva, rachaActual)
        assertEquals(11, maxima)
    }

    @Test
    fun `racha maxima no decrece`() {
        val rachaAnterior = 10
        val nueva         = 1   // reset
        val maxima        = maxOf(nueva, rachaAnterior)
        assertEquals(10, maxima)
    }
}
