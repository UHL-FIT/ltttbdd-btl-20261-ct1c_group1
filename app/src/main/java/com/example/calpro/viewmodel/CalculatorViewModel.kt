package com.example.calpro.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calpro.data.local.model.CalculationModel
import com.example.calpro.data.repository.CalculationRepository
import com.example.calpro.domain.engine.ExpressionEngine
import com.example.calpro.domain.engine.formatNumber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorState(
    val expression: String = "",
    val preview: String = "",
    val isScientificMode: Boolean = false,
    val isDegreeMode: Boolean = true,
    val errorMsg: String? = null
)

class CalculatorViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val engine: ExpressionEngine,
    private val repository: CalculationRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    companion object {
        private const val KEY_EXPRESSION = "calc_expression"
    }

    private val _state = MutableStateFlow(
        CalculatorState(
            expression = savedStateHandle.get<String>(KEY_EXPRESSION) ?: ""
        )
    )
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    private val SCI_MODE_KEY = booleanPreferencesKey("sci_mode")

    private val DRAFT_EXPR_KEY =
        androidx.datastore.preferences.core.stringPreferencesKey("draft_expression")

    init {
        viewModelScope.launch {
            // Lấy dữ liệu 1 lần lúc init
            val prefs = dataStore.data.first()
            val draft = prefs[DRAFT_EXPR_KEY] ?: ""

            // Chỉ khôi phục từ draft nếu app mở mới hoàn toàn (savedStateHandle trống)
            if (!savedStateHandle.contains(KEY_EXPRESSION) && draft.isNotEmpty()) {
                _state.update { it.copy(expression = draft) }
                calculatePreview(draft)
                savedStateHandle[KEY_EXPRESSION] = draft
            } else {
                // Khôi phục từ SavedStateHandle (khi xoay màn hình hoặc process death)
                val initialExpr = savedStateHandle.get<String>(KEY_EXPRESSION) ?: ""
                if (initialExpr.isNotEmpty()) {
                    calculatePreview(initialExpr)
                }
            }
        }

        viewModelScope.launch {
            dataStore.data.map { it[SCI_MODE_KEY] ?: false }.collect { mode ->
                _state.update { it.copy(isScientificMode = mode) }
            }
        }

        // Debounce lưu expression vào DataStore (mỗi 500ms) để không ghi ổ đĩa quá nhiều khi gõ nhanh
        viewModelScope.launch {
            _state.map { it.expression }
                .distinctUntilChanged()
                .debounce(500)
                .collect { expr ->
                    dataStore.edit { prefs ->
                        prefs[DRAFT_EXPR_KEY] = expr
                    }
                }
        }
    }

    fun onInput(char: String) {
        setExpression(_state.value.expression + char)
    }

    fun onDelete() {
        val cur = _state.value.expression
        if (cur.isNotEmpty()) {
            setExpression(cur.dropLast(1))
        }
    }

    fun onClear() {
        setExpression("")
    }

    fun onEvaluate() {
        val expr = _state.value.expression
        if (expr.isBlank()) return
        try {
            val res = engine.evaluate(expr, _state.value.isDegreeMode)
            val resultStr = formatNumber(res)
            savedStateHandle[KEY_EXPRESSION] = resultStr
            _state.update { it.copy(expression = resultStr, preview = "") }
            viewModelScope.launch {
                repository.insertCalculation(
                    CalculationModel(expression = expr, result = resultStr)
                )
            }
            // Thêm phần bắt lỗi ArithmeticException ở đây:
        } catch (e: ArithmeticException) {
            if (e.message?.contains("Division by zero", ignoreCase = true) == true) {
                _state.update { it.copy(errorMsg = "error_divide_zero") }
            } else {
                _state.update { it.copy(errorMsg = "error_syntax") }
            }
        } catch (e: Exception) {
            _state.update { it.copy(errorMsg = "error_syntax") }
        }
    }

    private fun calculatePreview(expr: String) {
        if (expr.isBlank()) {
            _state.update { it.copy(preview = "") }
            return
        }

        try {
            val res = engine.evaluate(expr, _state.value.isDegreeMode)
            val resStr = formatNumber(res)
            _state.update { it.copy(preview = resStr) }
        } catch (e: Exception) {
            _state.update { it.copy(preview = "") }
        }
    }

    fun toggleScientificMode() {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[SCI_MODE_KEY] = !(prefs[SCI_MODE_KEY] ?: false)
            }
        }
    }

    fun toggleAngleMode() {
        _state.update { it.copy(isDegreeMode = !it.isDegreeMode) }
        // Cập nhật lại preview khi chuyển chế độ góc
        calculatePreview(_state.value.expression)
    }

    fun clearError() {
        _state.update { it.copy(errorMsg = null) }
    }

    fun setExpression(expr: String) {
        savedStateHandle[KEY_EXPRESSION] = expr
        _state.update { it.copy(expression = expr, errorMsg = null) }
        calculatePreview(expr)
    }
}