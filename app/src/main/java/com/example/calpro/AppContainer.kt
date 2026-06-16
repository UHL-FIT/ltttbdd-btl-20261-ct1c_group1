package com.example.calpro

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.calpro.data.local.db.AppDatabase
import com.example.calpro.data.repository.CalculationRepository
import com.example.calpro.domain.engine.ExpressionEngine
import com.example.calpro.domain.engine.UnitConverter

/**
 * Container đơn giản để quản lý dependencies
 * Thay thế cho Hilt - dễ hiểu hơn cho beginner
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppContainer(private val context: Context) {

    // Database
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    // Repository
    val calculationRepository: CalculationRepository by lazy {
        CalculationRepository(database.calculationDao())
    }

    // Engines
    val expressionEngine: ExpressionEngine by lazy {
        ExpressionEngine()
    }

    val unitConverter: UnitConverter by lazy {
        UnitConverter()
    }

    // DataStore
    val dataStore: DataStore<Preferences> by lazy {
        context.dataStore
    }

    // Theme Repository
    val themeRepository: com.example.calpro.data.repository.ThemeRepository by lazy {
        com.example.calpro.data.repository.ThemeRepository(dataStore)
    }
}