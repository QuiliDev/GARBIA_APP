package com.garbia.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.garbia.app.data.local.dao.EscaneoDao
import com.garbia.app.data.local.dao.LogroDao
import com.garbia.app.data.local.dao.UsuarioDao
import com.garbia.app.data.local.entity.EscaneoEntity
import com.garbia.app.data.local.entity.LogroEntity
import com.garbia.app.data.local.entity.UsuarioEntity

@Database(
    entities = [UsuarioEntity::class, EscaneoEntity::class, LogroEntity::class],
    version = 2,
    exportSchema = false
)
abstract class GarbiaDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun escaneoDao(): EscaneoDao
    abstract fun logroDao(): LogroDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS logros (
                id TEXT NOT NULL PRIMARY KEY,
                nombre TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                emoji TEXT NOT NULL,
                desbloqueado INTEGER NOT NULL DEFAULT 0
            )"""
        )
    }
}
