package com.garbia.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests unitarios para la lógica de puntuación y niveles.
 * Replica las funciones privadas de UsuarioRepository para probarlas aisladas.
 */
class PuntuacionTest {

    // Replica de calcularNivel() en UsuarioRepository
    private fun calcularNivel(puntos: Int): Int = (puntos / 500) + 1

    // Replica de calcularLabelNivel() en UsuarioRepository
    private fun calcularLabelNivel(nivel: Int): String = when (nivel) {
        1    -> "Nivel 1: Principiante"
        2    -> "Nivel 2: Aprendiz"
        3    -> "Nivel 3: Reciclador"
        4    -> "Nivel 4: Experto"
        else -> "Nivel $nivel: Maestro"
    }

    @Test
    fun `nivel es 1 con 0 puntos`() {
        assertEquals(1, calcularNivel(0))
    }

    @Test
    fun `nivel es 1 con 499 puntos`() {
        assertEquals(1, calcularNivel(499))
    }

    @Test
    fun `nivel es 2 con exactamente 500 puntos`() {
        assertEquals(2, calcularNivel(500))
    }

    @Test
    fun `nivel es 2 con 999 puntos`() {
        assertEquals(2, calcularNivel(999))
    }

    @Test
    fun `nivel es 3 con 1000 puntos`() {
        assertEquals(3, calcularNivel(1000))
    }

    @Test
    fun `nivel es 4 con 1500 puntos`() {
        assertEquals(4, calcularNivel(1500))
    }

    @Test
    fun `label nivel 1 correcto`() {
        assertEquals("Nivel 1: Principiante", calcularLabelNivel(1))
    }

    @Test
    fun `label nivel 2 correcto`() {
        assertEquals("Nivel 2: Aprendiz", calcularLabelNivel(2))
    }

    @Test
    fun `label nivel 3 correcto`() {
        assertEquals("Nivel 3: Reciclador", calcularLabelNivel(3))
    }

    @Test
    fun `label nivel 4 correcto`() {
        assertEquals("Nivel 4: Experto", calcularLabelNivel(4))
    }

    @Test
    fun `label nivel 5 usa Maestro`() {
        assertTrue(calcularLabelNivel(5).contains("Maestro"))
    }

    @Test
    fun `puntos en nivel se calcula correctamente`() {
        val puntos = 750
        val puntosEnNivel = puntos % 500
        assertEquals(250, puntosEnNivel)
    }
}
