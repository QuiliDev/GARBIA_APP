package com.garbia.app.data.remote

import com.garbia.app.data.model.RankingEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor() {

    // Firestore instance — null if google-services.json is not configured
    private val db: FirebaseFirestore? = try {
        Firebase.firestore
    } catch (e: Exception) {
        null
    }

    suspend fun sincronizarUsuario(
        uid: String,
        nombre: String,
        puntos: Int,
        escaneos: Int,
        co2: Float
    ) {
        val firestore = db ?: return
        try {
            val data = mapOf(
                "nombre"          to nombre,
                "puntosTotales"   to puntos,
                "escaneosTotales" to escaneos,
                "co2Ahorrado"     to co2,
                "ultimaSync"      to System.currentTimeMillis()
            )
            firestore.collection("usuarios").document(uid).set(data).await()
        } catch (e: Exception) {
            // Sin conexión o sin configuración — el flujo local continúa sin interrupciones
        }
    }

    suspend fun getRanking(): List<RankingEntry> {
        val firestore = db ?: return mockRanking()
        return try {
            val snapshot = firestore.collection("usuarios")
                .orderBy("puntosTotales", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .await()

            snapshot.documents.mapIndexed { index, doc ->
                RankingEntry(
                    position  = index + 1,
                    name      = doc.getString("nombre") ?: "Usuario",
                    initials  = doc.getString("nombre")?.split(" ")
                        ?.mapNotNull { it.firstOrNull()?.toString() }
                        ?.take(2)?.joinToString("") ?: "?",
                    points    = (doc.getLong("puntosTotales") ?: 0).toInt(),
                    level     = calcularLabelNivel((doc.getLong("puntosTotales") ?: 0).toInt()),
                    firebaseUid = doc.id
                )
            }.ifEmpty { mockRanking() }
        } catch (e: Exception) {
            mockRanking()
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

    private fun mockRanking(): List<RankingEntry> = listOf(
        RankingEntry(1,  "María García",  "MG", 8_520, "Nivel 8: Maestro"),
        RankingEntry(2,  "Carlos López",  "CL", 7_240, "Nivel 7: Experto+"),
        RankingEntry(3,  "Ana Martínez",  "AM", 6_890, "Nivel 7: Experto+"),
        RankingEntry(4,  "Pedro Sánchez", "PS", 5_320, "Nivel 6: Avanzado"),
        RankingEntry(5,  "Laura Jiménez", "LJ", 4_980, "Nivel 6: Avanzado"),
        RankingEntry(6,  "Diego Torres",  "DT", 3_840, "Nivel 5: Experto"),
        RankingEntry(7,  "Sofía Ruiz",    "SR",   980, "Nivel 4: Intermedio"),
        RankingEntry(8,  "Miguel Herr.",  "MH",   750, "Nivel 3: Aprendiz"),
        RankingEntry(9,  "Elena Mora",    "EM",   620, "Nivel 2: Principiante"),
        RankingEntry(10, "Luis Vega",     "LV",   410, "Nivel 3: Aprendiz")
    )
}
