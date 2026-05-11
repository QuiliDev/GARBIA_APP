package com.garbia.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.garbia.app.data.local.entity.LogroEntity

@Dao
interface LogroDao {

    @Query("SELECT * FROM logros ORDER BY desbloqueado DESC, id ASC")
    fun getAllLogros(): Flow<List<LogroEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(logros: List<LogroEntity>)

    @Query("UPDATE logros SET desbloqueado = 1 WHERE id = :id AND desbloqueado = 0")
    suspend fun desbloquear(id: String)

    @Query("SELECT COUNT(*) FROM logros WHERE id = :id")
    suspend fun existe(id: String): Int
}
