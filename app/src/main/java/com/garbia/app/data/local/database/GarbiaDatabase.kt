package com.garbia.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.garbia.app.data.local.dao.EscaneoDao
import com.garbia.app.data.local.dao.UsuarioDao
import com.garbia.app.data.local.entity.EscaneoEntity
import com.garbia.app.data.local.entity.UsuarioEntity

@Database(
    entities = [UsuarioEntity::class, EscaneoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GarbiaDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun escaneoDao(): EscaneoDao
}
