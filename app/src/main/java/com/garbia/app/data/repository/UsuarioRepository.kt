package com.garbia.app.data.repository

import com.garbia.app.data.local.dao.EscaneoDao
import com.garbia.app.data.local.dao.UsuarioDao
import com.garbia.app.data.local.entity.EscaneoEntity
import com.garbia.app.data.local.entity.UsuarioEntity
import com.garbia.app.data.model.Escaneo
import com.garbia.app.data.model.Usuario
import com.garbia.app.data.remote.FirestoreService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepository @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val escaneoDao: EscaneoDao,
    private val firestoreService: FirestoreService,
    private val logrosRepository: LogrosRepository,
    private val rachaRepository: RachaRepository
) {
    fun getUsuario(): Flow<Usuario?> =
        usuarioDao.getUsuario().map { it?.toDomain() }

    fun getHistorialReciente(): Flow<List<Escaneo>> =
        escaneoDao.getHistorialReciente().map { list -> list.map { it.toDomain() } }

    suspend fun inicializarSiNecesario() {
        usuarioDao.crearSiNoExiste(UsuarioEntity(nombre = "Usuario"))
        logrosRepository.inicializarLogros()
    }

    suspend fun registrarEscaneo(
        tipoMaterial: String,
        contenedor: String,
        puntos: Int,
        co2: Float,
        fotoUri: String? = null
    ) {
        escaneoDao.insertEscaneo(
            EscaneoEntity(
                tipoMaterial = tipoMaterial,
                contenedor = contenedor,
                puntosGanados = puntos,
                co2Ahorrado = co2,
                fotoUri = fotoUri
            )
        )
        usuarioDao.sumarPuntos(puntos, co2)

        val usuario = usuarioDao.getUsuarioOnce()
        if (usuario != null) {
            rachaRepository.registrarEscaneoHoy()
            logrosRepository.verificarLogros(
                escaneos = usuario.escaneosTotales,
                puntos   = usuario.puntosTotales,
                co2      = usuario.co2Ahorrado
            )

            val uid = usuario.firebaseUid ?: "local_user"
            firestoreService.sincronizarUsuario(
                uid      = uid,
                nombre   = usuario.nombre,
                puntos   = usuario.puntosTotales,
                escaneos = usuario.escaneosTotales,
                co2      = usuario.co2Ahorrado
            )
        }
    }
}

private fun UsuarioEntity.toDomain(): Usuario {
    val nivel = calcularNivel(puntosTotales)
    return Usuario(
        id = id,
        nombre = nombre,
        puntosTotales = puntosTotales,
        escaneosTotales = escaneosTotales,
        co2Ahorrado = co2Ahorrado,
        nivel = nivel,
        nivelLabel = calcularLabelNivel(nivel),
        puntosEnNivel = puntosTotales % 300,
        puntosParaSiguienteNivel = 300
    )
}

private fun EscaneoEntity.toDomain() = Escaneo(
    id = id,
    tipoMaterial = tipoMaterial,
    contenedor = contenedor,
    puntosGanados = puntosGanados,
    co2Ahorrado = co2Ahorrado,
    fechaTimestamp = fechaTimestamp
)

private fun calcularNivel(puntos: Int): Int = (puntos / 300) + 1

private fun calcularLabelNivel(nivel: Int): String = when (nivel) {
    1 -> "Nivel 1: Principiante"
    2 -> "Nivel 2: Aprendiz"
    3 -> "Nivel 3: Reciclador"
    4 -> "Nivel 4: Experto"
    else -> "Nivel $nivel: Maestro"
}
