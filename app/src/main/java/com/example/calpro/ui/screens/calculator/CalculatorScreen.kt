package com.example.calpro.ui.screens.calculator

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.calpro.CalcApplication
import com.example.calpro.R
import com.example.calpro.ui.components.CalcButton
import com.example.calpro.ui.components.ExpressionDisplay
import com.example.calpro.ui.theme.CalcProTheme
import com.example.calpro.viewmodel.CalculatorState
import com.example.calpro.viewmodel.CalculatorViewModel
import com.example.calpro.viewmodel.ViewModelFactory

/**
 * Màn hình máy tính chính.
 */
@Composable
fun CalculatorScreen(navController: NavController? = null) {
    val context = LocalContext.current
    val app = context.applicationContext as CalcApplication

    val factory = remember {
        ViewModelFactory(
            app.appContainer.calculationRepository,
            app.appContainer.expressionEngine,
            app.appContainer.unitConverter,
            app.appContainer.dataStore
        )
    }
    val viewModel: CalculatorViewModel = viewModel(factory = factory)

    val state by viewModel.state.collectAsStateWithLifecycle()

    // Xử lý dữ liệu trả về từ HistoryScreen
    if (navController != null) {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val savedStateHandle = backStackEntry?.savedStateHandle

        // Theo dõi "history_expr" trong savedStateHandle
        val exprFromHistory by savedStateHandle?.getStateFlow<String?>("history_expr", null)
            ?.collectAsStateWithLifecycle(initialValue = null) ?: remember { mutableStateOf(null) }

        LaunchedEffect(exprFromHistory) {
            exprFromHistory?.let { expr ->
                viewModel.setExpression(expr)
                savedStateHandle?.remove<String>("history_expr") // Xóa sau khi đã xử lý
            }
        }
    }

    CalculatorContent(
        state = state,
        onInput = viewModel::onInput,
        onClear = viewModel::onClear,
        onDelete = viewModel::onDelete,
        onEvaluate = viewModel::onEvaluate,
        onToggleScientific = viewModel::toggleScientificMode,
        onToggleAngleMode = viewModel::toggleAngleMode,
        onClearError = viewModel::clearError
    )
}

@Composable
fun CalculatorContent(
    state: CalculatorState,
    onInput: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onEvaluate: () -> Unit,
    onToggleScientific: () -> Unit,
    onToggleAngleMode: () -> Unit,
    onClearError: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Khai báo sẵn các chuỗi lỗi
    val syntaxErrorStr = stringResource(id = R.string.error_syntax)
    val divideZeroStr = stringResource(id = R.string.error_divide_zero)

    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(state.errorMsg) {
        state.errorMsg?.let { errorKey ->
            // Kiểm tra mã lỗi từ ViewModel truyền sang để hiện đúng thông báo
            val messageToShow = if (errorKey == "error_divide_zero") {
                divideZeroStr
            } else {
                syntaxErrorStr
            }
            snackbarHostState.showSnackbar(messageToShow)
            onClearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (isLandscape) {
            LandscapeCalculatorLayout(
                state = state,
                onInput = onInput,
                onClear = onClear,
                onDelete = onDelete,
                onEvaluate = onEvaluate,
                onToggleScientific = onToggleScientific,
                onToggleAngleMode = onToggleAngleMode,
                modifier = Modifier.padding(padding)
            )
        } else {
            PortraitCalculatorLayout(
                state = state,
                onInput = onInput,
                onClear = onClear,
                onDelete = onDelete,
                onEvaluate = onEvaluate,
                onToggleScientific = onToggleScientific,
                onToggleAngleMode = onToggleAngleMode,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun PortraitCalculatorLayout(
    state: CalculatorState,
    onInput: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onEvaluate: () -> Unit,
    onToggleScientific: () -> Unit,
    onToggleAngleMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        ExpressionDisplay(
            expression = state.expression,
            preview = state.preview,
            modifier = Modifier.weight(1f),
            isLandscape = false
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onToggleScientific) {
                Text(
                    if (state.isScientificMode) stringResource(id = R.string.btn_toggle_basic)
                    else stringResource(id = R.string.btn_toggle_sci)
                )
            }

            // Nút DEG/RAD chỉ hiện khi ở chế độ khoa học
            if (state.isScientificMode) {
                TextButton(
                    onClick = onToggleAngleMode,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        if (state.isDegreeMode)
                            stringResource(id = R.string.btn_deg)
                        else
                            stringResource(id = R.string.btn_rad)
                    )
                }
            }
        }

        CalculatorButtonGrid(
            isScientificMode = state.isScientificMode,
            onInput = onInput,
            onClear = onClear,
            onDelete = onDelete,
            onEvaluate = onEvaluate,
            isLandscape = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

@Composable
private fun LandscapeCalculatorLayout(
    state: CalculatorState,
    onInput: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onEvaluate: () -> Unit,
    onToggleScientific: () -> Unit,
    onToggleAngleMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        // Left panel: display + toggle buttons
        Column(
            modifier = Modifier
                .weight(0.35f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            ExpressionDisplay(
                expression = state.expression,
                preview = state.preview,
                modifier = Modifier.weight(1f),
                isLandscape = true
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onToggleScientific) {
                    Text(
                        if (state.isScientificMode) stringResource(id = R.string.btn_toggle_basic)
                        else stringResource(id = R.string.btn_toggle_sci)
                    )
                }
                if (state.isScientificMode) {
                    TextButton(
                        onClick = onToggleAngleMode,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            if (state.isDegreeMode)
                                stringResource(id = R.string.btn_deg)
                            else
                                stringResource(id = R.string.btn_rad)
                        )
                    }
                }
            }
        }

        // Right panel: button grid that fills height
        LandscapeButtonGrid(
            isScientificMode = state.isScientificMode,
            onInput = onInput,
            onClear = onClear,
            onDelete = onDelete,
            onEvaluate = onEvaluate,
            modifier = Modifier
                .weight(0.65f)
                .fillMaxHeight()
                .padding(2.dp)
        )
    }
}

/**
 * Grid nút bấm cho chế độ landscape - sử dụng Column+Row thay vì LazyVerticalGrid
 * để các nút chia đều chiều cao màn hình, không cần cuộn.
 */
@Composable
private fun LandscapeButtonGrid(
    isScientificMode: Boolean,
    onInput: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onEvaluate: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = isScientificMode,
        modifier = modifier,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
        contentAlignment = Alignment.Center,
        label = "LandscapeButtonGridAnimation"
    ) { sciMode ->
        val buttons = if (sciMode) {
            getSciButtons(onInput, onClear, onDelete, onEvaluate)
        } else {
            getBasicButtons(onInput, onClear, onDelete, onEvaluate)
        }
        val cols = if (sciMode) 5 else 4
        val rows = buttons.chunked(cols)

        Column(modifier = Modifier.fillMaxSize()) {
            rows.forEach { rowButtons ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    rowButtons.forEach { (resId, action) ->
                        val isACBtn = resId == R.string.btn_ac
                        val isDelBtn = resId == R.string.btn_del

                        val bgColor = when {
                            isACBtn -> MaterialTheme.colorScheme.errorContainer
                            isDelBtn -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        val txtColor = when {
                            isACBtn -> MaterialTheme.colorScheme.onErrorContainer
                            isDelBtn -> MaterialTheme.colorScheme.onSecondaryContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        CalcButton(
                            label = stringResource(id = resId),
                            isLandscape = true,
                            containerColor = bgColor,
                            contentColor = txtColor,
                            onClick = action,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Nếu hàng cuối thiếu nút thì fill phần trống
                    val remaining = cols - rowButtons.size
                    if (remaining > 0) {
                        Spacer(modifier = Modifier.weight(remaining.toFloat()))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatorButtonGrid(
    isScientificMode: Boolean,
    onInput: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onEvaluate: () -> Unit,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = isScientificMode,
        modifier = modifier,
        transitionSpec = {
            fadeIn().togetherWith(fadeOut())
        },
        contentAlignment = Alignment.BottomCenter,
        label = "ButtonGridAnimation"
    ) { sciMode ->
        val buttons = if (sciMode) {
            getSciButtons(onInput, onClear, onDelete, onEvaluate)
        } else {
            getBasicButtons(onInput, onClear, onDelete, onEvaluate)
        }
        val cols = if (sciMode) 5 else 4

        LazyVerticalGrid(
            columns = GridCells.Fixed(cols),
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = false,
            verticalArrangement = Arrangement.Bottom
        ) {
            items(buttons) { (resId, action) ->
                val isACBtn = resId == R.string.btn_ac
                val isDelBtn = resId == R.string.btn_del

                val bgColor = when {
                    isACBtn -> MaterialTheme.colorScheme.errorContainer
                    isDelBtn -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                val txtColor = when {
                    isACBtn -> MaterialTheme.colorScheme.onErrorContainer
                    isDelBtn -> MaterialTheme.colorScheme.onSecondaryContainer
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                CalcButton(
                    label = stringResource(id = resId),
                    isLandscape = isLandscape,
                    containerColor = bgColor,
                    contentColor = txtColor,
                    onClick = action
                )
            }
        }
    }
}

private fun getBasicButtons(
    onInput: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onEvaluate: () -> Unit
): List<Pair<Int, () -> Unit>> {
    return listOf(
        R.string.btn_ac to onClear,
        R.string.btn_open_paren to { onInput("(") },
        R.string.btn_close_paren to { onInput(")") },
        R.string.btn_div to { onInput("÷") },
        R.string.btn_7 to { onInput("7") },
        R.string.btn_8 to { onInput("8") },
        R.string.btn_9 to { onInput("9") },
        R.string.btn_mul to { onInput("×") },
        R.string.btn_4 to { onInput("4") },
        R.string.btn_5 to { onInput("5") },
        R.string.btn_6 to { onInput("6") },
        R.string.btn_minus to { onInput("-") },
        R.string.btn_1 to { onInput("1") },
        R.string.btn_2 to { onInput("2") },
        R.string.btn_3 to { onInput("3") },
        R.string.btn_plus to { onInput("+") },
        R.string.btn_dot to { onInput(".") },
        R.string.btn_0 to { onInput("0") },
        R.string.btn_del to onDelete,
        R.string.btn_equal to onEvaluate
    )
}

private fun getSciButtons(
    onInput: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onEvaluate: () -> Unit
): List<Pair<Int, () -> Unit>> {
    return listOf(
        R.string.btn_sin to { onInput("sin(") },
        R.string.btn_cos to { onInput("cos(") },
        R.string.btn_tan to { onInput("tan(") },
        R.string.btn_ac to onClear,
        R.string.btn_del to onDelete,
        R.string.btn_log to { onInput("log10(") },
        R.string.btn_ln to { onInput("ln(") },
        R.string.btn_sqrt to { onInput("sqrt(") },
        R.string.btn_open_paren to { onInput("(") },
        R.string.btn_close_paren to { onInput(")") },
        R.string.btn_pi to { onInput("π") },
        R.string.btn_7 to { onInput("7") },
        R.string.btn_8 to { onInput("8") },
        R.string.btn_9 to { onInput("9") },
        R.string.btn_div to { onInput("÷") },
        R.string.btn_e to { onInput("e") },
        R.string.btn_4 to { onInput("4") },
        R.string.btn_5 to { onInput("5") },
        R.string.btn_6 to { onInput("6") },
        R.string.btn_mul to { onInput("×") },
        R.string.btn_pow to { onInput("^") },
        R.string.btn_1 to { onInput("1") },
        R.string.btn_2 to { onInput("2") },
        R.string.btn_3 to { onInput("3") },
        R.string.btn_minus to { onInput("-") },
        R.string.btn_mod to { onInput("%") },
        R.string.btn_dot to { onInput(".") },
        R.string.btn_0 to { onInput("0") },
        R.string.btn_equal to onEvaluate,
        R.string.btn_plus to { onInput("+") }
    )
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CalcProTheme {
        CalculatorContent(
            state = CalculatorState(
                expression = "12+34",
                preview = "46",
                isScientificMode = false
            ),
            onInput = {},
            onClear = {},
            onDelete = {},
            onEvaluate = {},
            onToggleScientific = {},
            onToggleAngleMode = {},
            onClearError = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorSciPreview() {
    CalcProTheme {
        CalculatorContent(
            state = CalculatorState(
                expression = "sin(30) + ",
                preview = "0.5",
                isScientificMode = true,
                isDegreeMode = true
            ),
            onInput = {},
            onClear = {},
            onDelete = {},
            onEvaluate = {},
            onToggleScientific = {},
            onToggleAngleMode = {},
            onClearError = {}
        )
    }
}
