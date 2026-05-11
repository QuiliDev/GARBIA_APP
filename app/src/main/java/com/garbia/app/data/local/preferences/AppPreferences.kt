package com.garbia.app.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

@Singleton
class AppPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    val hasSeenOnboarding: Flow<Boolean> = context.appDataStore.data
        .map { prefs -> prefs[Keys.HAS_SEEN_ONBOARDING] ?: false }

    suspend fun setHasSeenOnboarding(value: Boolean) {
        context.appDataStore.edit { it[Keys.HAS_SEEN_ONBOARDING] = value }
    }

    private object Keys {
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
    }
}
