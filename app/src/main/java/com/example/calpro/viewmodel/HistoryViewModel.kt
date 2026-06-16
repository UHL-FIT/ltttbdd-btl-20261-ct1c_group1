package com.example.calpro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calpro.data.local.model.CalculationModel
import com.example.calpro.data.repository.CalculationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: CalculationRepository
) : ViewModel() {

    val history: StateFlow<List<CalculationModel>> = repository.getHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteItem(model: CalculationModel) {
        viewModelScope.launch {
            repository.deleteCalculation(model)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
