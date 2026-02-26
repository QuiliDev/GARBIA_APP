package com.garbia.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.garbia.app.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuario WHERE id = 1")
    fun getUsuario(): Flow<UsuarioEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun crearSiNoExiste(usuario: UsuarioEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(usuario: UsuarioEntity)

    @Query("UPDATE usuario SET puntosTotales = puntosTotales + :puntos, escaneosTotales = escaneosTotales + 1, co2Ahorrado = co2Ahorrado + :co2 WHERE id = 1")
    suspend fun sumarPuntos(puntos: Int, co2: Float)
}
