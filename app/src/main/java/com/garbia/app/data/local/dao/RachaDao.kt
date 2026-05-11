package com.garbia.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.garbia.app.data.local.entity.RachaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RachaDao {

    @Query("SELECT * FROM racha WHERE id = 1")
    fun getRacha(): Flow<RachaEntity?>

    @Query("SELECT * FROM racha WHERE id = 1")
    suspend fun getRachaOnce(): RachaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(racha: RachaEntity)
}
