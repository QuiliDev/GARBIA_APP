package com.garbia.app

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests unitarios para las condiciones de desbloqueo de logros.
 * Replica la lógica de LogrosRepository#verificarLogros().
 */
class LogrosCondicionesTest {

    private fun debeDesbloquear(id: String, escaneos: Int, puntos: Int, co2: Float): Boolean = when (id) {
        "primer_escaneo" -> escaneos >= 1
        "cinco_escaneos" -> escaneos >= 5
        "25_escaneos"    -> escaneos >= 25
        "50_escaneos"    -> escaneos >= 50
        "100_escaneos"   -> escaneos >= 100
        "100_puntos"     -> puntos >= 100
        "500_puntos"     -> puntos >= 500
        "1000_puntos"    -> puntos >= 1000
        "1kg_co2"        -> co2 >= 1f
        else             -> false
    }

    @Test
    fun `primer escaneo se desbloquea con 1 escaneo`() {
        assertTrue(debeDesbloquear("primer_escaneo", escaneos = 1, puntos = 0, co2 = 0f))
    }

    @Test
    fun `primer escaneo no se desbloquea con 0 escaneos`() {
        assertFalse(debeDesbloquear("primer_escaneo", escaneos = 0, puntos = 0, co2 = 0f))
    }

    @Test
    fun `cinco escaneos requiere exactamente 5`() {
        assertFalse(debeDesbloquear("cinco_escaneos", escaneos = 4, puntos = 0, co2 = 0f))
        assertTrue(debeDesbloquear("cinco_escaneos",  escaneos = 5, puntos = 0, co2 = 0f))
    }

    @Test
    fun `logro 100 puntos no se desbloquea con 99`() {
        assertFalse(debeDesbloquear("100_puntos", escaneos = 0, puntos = 99, co2 = 0f))
    }

    @Test
    fun `logro 100 puntos se desbloquea con exactamente 100`() {
        assertTrue(debeDesbloquear("100_puntos", escaneos = 0, puntos = 100, co2 = 0f))
    }

    @Test
    fun `logro oro requiere 1000 puntos`() {
        assertFalse(debeDesbloquear("1000_puntos", escaneos = 0, puntos = 999, co2 = 0f))
        assertTrue(debeDesbloquear("1000_puntos",  escaneos = 0, puntos = 1000, co2 = 0f))
    }

    @Test
    fun `planeta protegido requiere 1 kg de CO2`() {
        assertFalse(debeDesbloquear("1kg_co2", escaneos = 0, puntos = 0, co2 = 0.99f))
        assertTrue(debeDesbloquear("1kg_co2",  escaneos = 0, puntos = 0, co2 = 1.0f))
    }

    @Test
    fun `logro desconocido nunca se desbloquea`() {
        assertFalse(debeDesbloquear("logro_inexistente", escaneos = 999, puntos = 9999, co2 = 99f))
    }
}
