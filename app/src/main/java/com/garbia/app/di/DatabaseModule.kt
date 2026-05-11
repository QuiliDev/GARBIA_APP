package com.garbia.app.di

import android.content.Context
import androidx.room.Room
import com.garbia.app.data.local.dao.EscaneoDao
import com.garbia.app.data.local.dao.LogroDao
import com.garbia.app.data.local.dao.RachaDao
import com.garbia.app.data.local.dao.UsuarioDao
import com.garbia.app.data.local.database.GarbiaDatabase
import com.garbia.app.data.local.database.MIGRATION_1_2
import com.garbia.app.data.local.database.MIGRATION_2_3
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GarbiaDatabase =
        Room.databaseBuilder(context, GarbiaDatabase::class.java, "garbia_db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()

    @Provides
    fun provideUsuarioDao(db: GarbiaDatabase): UsuarioDao = db.usuarioDao()

    @Provides
    fun provideEscaneoDao(db: GarbiaDatabase): EscaneoDao = db.escaneoDao()

    @Provides
    fun provideLogroDao(db: GarbiaDatabase): LogroDao = db.logroDao()

    @Provides
    fun provideRachaDao(db: GarbiaDatabase): RachaDao = db.rachaDao()
}
