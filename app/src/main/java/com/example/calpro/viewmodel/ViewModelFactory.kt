package com.example.calpro.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.calpro.data.repository.CalculationRepository
import com.example.calpro.domain.engine.ExpressionEngine
import com.example.calpro.domain.engine.UnitConverter

class ViewModelFactory(
    private val repository: CalculationRepository,
    private val expressionEngine: ExpressionEngine,
    private val unitConverter: UnitConverter,
    private val dataStore: DataStore<Preferences>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(CalculatorViewModel::class.java) -> {
                val savedStateHandle = extras.createSavedStateHandle()
                CalculatorViewModel(savedStateHandle, expressionEngine, repository, dataStore) as T
            }

            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ConverterViewModel::class.java) -> {
                ConverterViewModel(unitConverter) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
