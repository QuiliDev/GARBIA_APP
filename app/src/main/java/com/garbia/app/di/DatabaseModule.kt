package com.garbia.app.di

import android.content.Context
import androidx.room.Room
import com.garbia.app.data.local.dao.EscaneoDao
import com.garbia.app.data.local.dao.UsuarioDao
import com.garbia.app.data.local.database.GarbiaDatabase
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
        Room.databaseBuilder(context, GarbiaDatabase::class.java, "garbia_db").build()

    @Provides
    fun provideUsuarioDao(db: GarbiaDatabase): UsuarioDao = db.usuarioDao()

    @Provides
    fun provideEscaneoDao(db: GarbiaDatabase): EscaneoDao = db.escaneoDao()
}
