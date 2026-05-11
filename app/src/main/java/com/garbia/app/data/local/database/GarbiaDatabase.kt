package com.garbia.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.garbia.app.data.local.dao.EscaneoDao
import com.garbia.app.data.local.dao.LogroDao
import com.garbia.app.data.local.dao.RachaDao
import com.garbia.app.data.local.dao.UsuarioDao
import com.garbia.app.data.local.entity.EscaneoEntity
import com.garbia.app.data.local.entity.LogroEntity
import com.garbia.app.data.local.entity.RachaEntity
import com.garbia.app.data.local.entity.UsuarioEntity

@Database(
    entities = [UsuarioEntity::class, EscaneoEntity::class, LogroEntity::class, RachaEntity::class],
    version = 3,
    exportSchema = false
)
abstract class GarbiaDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun escaneoDao(): EscaneoDao
    abstract fun logroDao(): LogroDao
    abstract fun rachaDao(): RachaDao
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

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS racha (
                id INTEGER NOT NULL PRIMARY KEY,
                rachaActual INTEGER NOT NULL DEFAULT 0,
                rachaMáxima INTEGER NOT NULL DEFAULT 0,
                ultimoEscaneoTimestamp INTEGER NOT NULL DEFAULT 0
            )"""
        )
    }
}
