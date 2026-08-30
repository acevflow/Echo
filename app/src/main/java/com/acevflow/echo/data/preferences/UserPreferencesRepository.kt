package com.acevflow.echo.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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
        val THEME_MODE = intPreferencesKey("theme_mode")
        val DYNAMIC_COLOR_ENABLED = booleanPreferencesKey("dynamic_color_enabled")
        val EQUALIZER_ENABLED = booleanPreferencesKey("equalizer_enabled")
        val EQUALIZER_BANDS = stringPreferencesKey("equalizer_bands") // Format: "band0:gain0,band1:gain1..."
    }

    val shuffleModeEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SHUFFLE_MODE_ENABLED] ?: false
    }

    val repeatMode: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.REPEAT_MODE] ?: 0 // 0 = REPEAT_MODE_OFF
    }

    val themeMode: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.THEME_MODE] ?: 0 // 0 = System, 1 = Light, 2 = Dark
    }

    val dynamicColorEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DYNAMIC_COLOR_ENABLED] ?: true
    }

    val equalizerEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.EQUALIZER_ENABLED] ?: false
    }

    val equalizerBands: Flow<Map<Int, Int>> = dataStore.data.map { preferences ->
        val bandsStr = preferences[PreferencesKeys.EQUALIZER_BANDS] ?: ""
        if (bandsStr.isEmpty()) emptyMap()
        else {
            bandsStr.split(",").mapNotNull {
                val parts = it.split(":")
                if (parts.size == 2) parts[0].toInt() to parts[1].toInt() else null
            }.toMap()
        }
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

    suspend fun setThemeMode(themeMode: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode
        }
    }

    suspend fun setDynamicColorEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DYNAMIC_COLOR_ENABLED] = enabled
        }
    }

    suspend fun setEqualizerEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.EQUALIZER_ENABLED] = enabled
        }
    }

    suspend fun setEqualizerBand(band: Int, gain: Int) {
        dataStore.edit { preferences ->
            val currentBands = preferences[PreferencesKeys.EQUALIZER_BANDS] ?: ""
            val bandsMap = if (currentBands.isEmpty()) mutableMapOf()
            else currentBands.split(",").mapNotNull {
                val parts = it.split(":")
                if (parts.size == 2) parts[0].toInt() to parts[1].toInt() else null
            }.toMap().toMutableMap()

            bandsMap[band] = gain
            preferences[PreferencesKeys.EQUALIZER_BANDS] = bandsMap.entries.joinToString(",") { "${it.key}:${it.value}" }
        }
    }
}
