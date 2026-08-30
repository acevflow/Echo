package com.acevflow.echo.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val SHUFFLE_MODE_ENABLED = booleanPreferencesKey("shuffle_mode_enabled")
        val REPEAT_MODE = intPreferencesKey("repeat_mode")
    }

    val shuffleModeEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SHUFFLE_MODE_ENABLED] ?: false
    }

    val repeatMode: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.REPEAT_MODE] ?: 0 // 0 = REPEAT_MODE_OFF
    }

    suspend fun setShuffleModeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHUFFLE_MODE_ENABLED] = enabled
        }
    }

    suspend fun setRepeatMode(repeatMode: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.REPEAT_MODE] = repeatMode
        }
    }
}
