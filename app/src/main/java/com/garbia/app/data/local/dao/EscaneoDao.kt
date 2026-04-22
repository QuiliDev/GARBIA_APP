package com.garbia.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.garbia.app.data.local.entity.EscaneoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EscaneoDao {

    @Query("SELECT * FROM escaneos ORDER BY fechaTimestamp DESC LIMIT 10")
    fun getHistorialReciente(): Flow<List<EscaneoEntity>>

    @Insert
    suspend fun insertEscaneo(escaneo: EscaneoEntity): Long

    @Query("SELECT COUNT(*) FROM escaneos WHERE tipoMaterial = :tipo")
    suspend fun contarPorTipo(tipo: String): Int

    @Query("SELECT * FROM escaneos WHERE fechaTimestamp >= :desde ORDER BY fechaTimestamp ASC")
    suspend fun getEscaneosDesdeFecha(desde: Long): List<EscaneoEntity>
}
