package com.garbia.app.data.repository

import com.garbia.app.data.local.dao.UsuarioDao
import com.garbia.app.data.model.RankingEntry
import com.garbia.app.data.remote.FirestoreService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RankingRepository @Inject constructor(
    private val firestoreService: FirestoreService,
    private val usuarioDao: UsuarioDao
) {
    fun getRanking(currentUserUid: String = "local_user"): Flow<List<RankingEntry>> = flow {
        val remoteList = firestoreService.getRanking()

        // Inject local user into ranking if not already present
        val localUsuario = usuarioDao.getUsuarioOnce()
        if (localUsuario != null) {
            val alreadyInList = remoteList.any { it.firebaseUid == currentUserUid }
            val merged = if (alreadyInList) {
                remoteList.mapIndexed { i, e ->
                    if (e.firebaseUid == currentUserUid) e.copy(isCurrentUser = true, position = i + 1)
                    else e.copy(position = i + 1)
                }
            } else {
                val localEntry = RankingEntry(
                    position     = 0,
                    name         = localUsuario.nombre,
                    initials     = localUsuario.nombre.split(" ")
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .take(2).joinToString(""),
                    points       = localUsuario.puntosTotales,
                    level        = calcularLabelNivel(localUsuario.puntosTotales),
                    isCurrentUser = true,
                    firebaseUid  = currentUserUid
                )
                (remoteList + localEntry)
                    .sortedByDescending { it.points }
                    .mapIndexed { i, e -> e.copy(position = i + 1) }
            }
            emit(merged)
        } else {
            emit(remoteList)
        }
    }

    private fun calcularLabelNivel(puntos: Int): String = when {
        puntos >= 10_000 -> "Nivel 10: Leyenda"
        puntos >= 7_000  -> "Nivel 8: Maestro"
        puntos >= 5_000  -> "Nivel 7: Experto+"
        puntos >= 3_000  -> "Nivel 6: Avanzado"
        puntos >= 1_500  -> "Nivel 5: Experto"
        puntos >= 800    -> "Nivel 4: Intermedio"
        puntos >= 400    -> "Nivel 3: Aprendiz"
        puntos >= 150    -> "Nivel 2: Principiante"
        else             -> "Nivel 1: Novato"
    }
}
