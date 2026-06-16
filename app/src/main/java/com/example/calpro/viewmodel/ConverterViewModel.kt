package com.example.calpro.viewmodel

import androidx.lifecycle.ViewModel
import com.example.calpro.domain.engine.UnitConverter
import com.example.calpro.domain.engine.formatExpressionForDisplay
import com.example.calpro.domain.engine.formatNumber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ConverterState(
    val category: String = "Length",
    val inputValue: String = "",
    val fromUnit: String = "m",
    val toUnit: String = "km",
    val result: String = "",
    val activeField: Int = 0
)

class ConverterViewModel(
    private val converter: UnitConverter
) : ViewModel() {

    private val _state = MutableStateFlow(ConverterState())
    val state: StateFlow<ConverterState> = _state.asStateFlow()

    fun updateInput(value: String) {

        val rawValue = value.replace(",", "")

        val formattedValue = formatExpressionForDisplay(rawValue)

        _state.update { it.copy(inputValue = formattedValue) }
        calculateForward()
    }

    fun updateResult(value: String) {

        val rawValue = value.replace(",", "")

        val formattedValue = formatExpressionForDisplay(rawValue)

        _state.update { it.copy(result = formattedValue) }
        calculateBackward()
    }

    fun setCategory(category: String, defaultFrom: String, defaultTo: String) {
        _state.update {
            it.copy(
                category = category,
                fromUnit = defaultFrom,
                toUnit = defaultTo,
                inputValue = "",
                result = ""
            )
        }
    }

    fun setFromUnit(unit: String) {
        _state.update { it.copy(fromUnit = unit) }
        calculateForward()
    }

    fun setToUnit(unit: String) {
        _state.update { it.copy(toUnit = unit) }
        calculateForward()
    }

    fun swapUnits() {
        _state.update { it.copy(fromUnit = it.toUnit, toUnit = it.fromUnit) }
        calculateForward()
    }

    private fun calculateForward() {
        val s = _state.value
        val dVal = s.inputValue.replace(",", "").toDoubleOrNull()

        if (dVal == null) {
            _state.update { it.copy(result = "") }
            return
        }

        val res = converter.convert(dVal, s.fromUnit, s.toUnit, s.category)
        _state.update {
            it.copy(result = formatNumber(res))
        }
    }

    private fun calculateBackward() {
        val s = _state.value
        val dVal = s.result.replace(",", "").toDoubleOrNull()

        if (dVal == null) {
            _state.update { it.copy(inputValue = "") }
            return
        }

        val res = converter.convert(dVal, s.toUnit, s.fromUnit, s.category)
        _state.update {
            it.copy(inputValue = formatNumber(res))
        }
    }

    fun setActiveField(fieldId: Int) {
        _state.update { it.copy(activeField = fieldId) }
    }

    fun onInput(char: String) {
        val s = _state.value
        if (s.activeField == 0) {
            updateInput(s.inputValue + char)
        } else {
            updateResult(s.result + char)
        }
    }

    fun onClear() {
        if (_state.value.activeField == 0) updateInput("")
        else updateResult("")
    }

    fun onDelete() {
        val s = _state.value
        if (s.activeField == 0) {
            if (s.inputValue.isNotEmpty()) updateInput(s.inputValue.dropLast(1))
        } else {
            if (s.result.isNotEmpty()) updateResult(s.result.dropLast(1))
        }
    }

    fun onToggleSign() {
        val s = _state.value
        if (s.activeField == 0) {
            val cur = s.inputValue
            if (cur.startsWith("-")) updateInput(cur.drop(1))
            else updateInput("-$cur")
        } else {
            val cur = s.result
            if (cur.startsWith("-")) updateResult(cur.drop(1))
            else updateResult("-$cur")
        }
    }
}