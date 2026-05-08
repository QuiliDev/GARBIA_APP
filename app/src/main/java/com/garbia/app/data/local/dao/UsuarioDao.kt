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

    @Query("SELECT * FROM usuario WHERE id = 1")
    suspend fun getUsuarioOnce(): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun crearSiNoExiste(usuario: UsuarioEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(usuario: UsuarioEntity)

    @Query("UPDATE usuario SET puntosTotales = puntosTotales + :puntos, escaneosTotales = escaneosTotales + 1, co2Ahorrado = co2Ahorrado + :co2 WHERE id = 1")
    suspend fun sumarPuntos(puntos: Int, co2: Float)

    @Query("UPDATE usuario SET nombre = :nombre WHERE id = 1")
    suspend fun actualizarNombre(nombre: String)

    @Query("UPDATE usuario SET puntosTotales = MAX(0, puntosTotales - :puntos) WHERE id = 1")
    suspend fun restarPuntos(puntos: Int)
}
