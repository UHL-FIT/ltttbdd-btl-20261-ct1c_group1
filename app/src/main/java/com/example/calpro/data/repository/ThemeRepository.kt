package com.example.calpro.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

class ThemeRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val THEME_MODE_KEY = intPreferencesKey("app_theme_mode_int")
    }

    val themeModeFlow: Flow<ThemeMode> = dataStore.data.map { preferences ->
        val modeInt = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.ordinal
        ThemeMode.values().getOrElse(modeInt) { ThemeMode.SYSTEM }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.ordinal
        }
    }
}