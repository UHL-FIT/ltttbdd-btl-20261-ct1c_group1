package com.example.calpro.ui.screens.converter

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calpro.CalcApplication
import com.example.calpro.R
import com.example.calpro.ui.components.CalcButton
import com.example.calpro.ui.theme.CalcProTheme
import com.example.calpro.viewmodel.ConverterState
import com.example.calpro.viewmodel.ConverterViewModel
import com.example.calpro.viewmodel.ViewModelFactory

// Màn hình Chuyển đổi đơn vị.

@Composable
fun ConverterScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as CalcApplication

    // Khởi tạo ViewModel
    val factory = remember {
        ViewModelFactory(
            app.appContainer.calculationRepository,
            app.appContainer.expressionEngine,
            app.appContainer.unitConverter,
            app.appContainer.dataStore
        )
    }
    val viewModel: ConverterViewModel = viewModel(factory = factory)


    // Lấy dữ liệu hiện tại
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isError =
        state.inputValue.isNotEmpty() && state.inputValue.replace(",", "").toDoubleOrNull() == null

    ConverterContent(
        state = state,
        isError = isError,
        onCategoryChange = { cat, from, to -> viewModel.setCategory(cat, from, to) },
        onSwapUnits = { viewModel.swapUnits() },
        onFromUnitChange = { viewModel.setFromUnit(it) },
        onToUnitChange = { viewModel.setToUnit(it) },
        onActiveFieldChange = { viewModel.setActiveField(it) },
        onInput = { viewModel.onInput(it) },
        onClear = { viewModel.onClear() },
        onDelete = { viewModel.onDelete() },
        onToggleSign = { viewModel.onToggleSign() }
    )
}

@Composable
fun ConverterContent(
    state: ConverterState,
    isError: Boolean,
    onCategoryChange: (String, String, String) -> Unit,
    onSwapUnits: () -> Unit,
    onFromUnitChange: (String) -> Unit,
    onToUnitChange: (String) -> Unit,
    onActiveFieldChange: (Int) -> Unit,
    onInput: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onToggleSign: () -> Unit
) {
    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        LandscapeConverterLayout(
            state, isError, onCategoryChange, onSwapUnits, onFromUnitChange, onToUnitChange,
            state.activeField, onActiveFieldChange,
            onInput, onClear, onDelete, onToggleSign
        )
    } else {
        PortraitConverterLayout(
            state, isError, onCategoryChange, onSwapUnits, onFromUnitChange, onToUnitChange,
            state.activeField, onActiveFieldChange,
            onInput, onClear, onDelete, onToggleSign
        )
    }
}

@Composable
private fun PortraitConverterLayout(
    state: ConverterState,
    isError: Boolean,
    onCategoryChange: (String, String, String) -> Unit,
    onSwapUnits: () -> Unit,
    onFromUnitChange: (String) -> Unit,
    onToUnitChange: (String) -> Unit,
    activeField: Int,
    onActiveFieldChange: (Int) -> Unit,
    onInput: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onToggleSign: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Hàng chứa các nút chọn danh mục
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryButtons(
                state = state,
                onCategoryChange = onCategoryChange,
                buttonModifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Khối hiển thị
        ConverterInputOutput(
            state = state,
            isError = isError,
            onSwapUnits = onSwapUnits,
            onFromUnitChange = onFromUnitChange,
            onToUnitChange = onToUnitChange,
            activeField = activeField,
            onActiveFieldChange = onActiveFieldChange
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bàn phím custom
        ConverterButtonGrid(
            onInput = onInput,
            onClear = onClear,
            onDelete = onDelete,
            onToggleSign = onToggleSign,
            allowNegative = state.category == "Temp",
            isLandscape = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LandscapeConverterLayout(
    state: ConverterState,
    isError: Boolean,
    onCategoryChange: (String, String, String) -> Unit,
    onSwapUnits: () -> Unit,
    onFromUnitChange: (String) -> Unit,
    onToUnitChange: (String) -> Unit,
    activeField: Int,
    onActiveFieldChange: (Int) -> Unit,
    onInput: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onToggleSign: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryButtons(
                    state = state,
                    onCategoryChange = onCategoryChange,
                    buttonModifier = Modifier.weight(1f)
                )
            }

            ConverterInputOutput(
                state = state,
                isError = isError,
                onSwapUnits = onSwapUnits,
                onFromUnitChange = onFromUnitChange,
                onToUnitChange = onToUnitChange,
                activeField = activeField,
                onActiveFieldChange = onActiveFieldChange
            )
        }

        ConverterButtonGrid(
            onInput = onInput,
            onClear = onClear,
            onDelete = onDelete,
            onToggleSign = onToggleSign,
            allowNegative = state.category == "Temp",
            isLandscape = true,
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight()
                .padding(start = 16.dp)
        )
    }
}

@Composable
private fun CategoryButtons(
    state: ConverterState,
    onCategoryChange: (String, String, String) -> Unit,
    buttonModifier: Modifier = Modifier
) {
    val options = listOf(
        Triple("Length", R.string.category_length, Triple("Length", "m", "km")),
        Triple("Weight", R.string.category_weight, Triple("Weight", "kg", "g")),
        Triple("Temp", R.string.category_temp, Triple("Temp", "°C", "°F"))
    )

    options.forEach { (catId, labelRes, args) ->
        val isSelected = state.category == catId
        if (isSelected) {
            Button(
                onClick = { onCategoryChange(args.first, args.second, args.third) },
                modifier = buttonModifier,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Text(stringResource(labelRes), maxLines = 1)
            }
        } else {
            OutlinedButton(
                onClick = { onCategoryChange(args.first, args.second, args.third) },
                modifier = buttonModifier,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Text(stringResource(labelRes), maxLines = 1)
            }
        }
    }
}

@Composable
private fun UnitDropdown(
    label: String,
    currentUnit: String,
    units: List<String>,
    onUnitSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(currentUnit)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit) },
                        onClick = {
                            onUnitSelected(unit)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConverterInputOutput(
    state: ConverterState,
    isError: Boolean,
    onSwapUnits: () -> Unit,
    onFromUnitChange: (String) -> Unit,
    onToUnitChange: (String) -> Unit,
    activeField: Int,
    onActiveFieldChange: (Int) -> Unit
) {
    val units = when (state.category) {
        "Length" -> listOf("nm", "µm", "mm", "cm", "dm", "m", "km", "inch", "ft", "yd", "mi", "nmi")
        "Weight" -> listOf("µg", "mg", "g", "kg", "t", "oz", "lb", "st")
        "Temp" -> listOf("°C", "°F", "K")
        else -> emptyList()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        UnitDropdown(
            label = stringResource(R.string.from_unit),
            currentUnit = state.fromUnit,
            units = units,
            onUnitSelected = onFromUnitChange
        )

        OutlinedIconButton(
            onClick = onSwapUnits,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = stringResource(R.string.swap),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }

        UnitDropdown(
            label = stringResource(R.string.to_unit),
            currentUnit = state.toUnit,
            units = units,
            onUnitSelected = onToUnitChange
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Ô nhập từ
    OutlinedTextField(
        value = state.inputValue,
        onValueChange = {},
        label = { Text(stringResource(R.string.from_unit)) },
        isError = isError && activeField == 0,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { if (it.isFocused) onActiveFieldChange(0) },
        textStyle = TextStyle(fontSize = 24.sp),
        singleLine = true,
        readOnly = true // Use custom keyboard instead
    )

    if (isError && activeField == 0) {
        Text(
            stringResource(R.string.error_invalid_number),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Ô nhập đến (kết quả)
    OutlinedTextField(
        value = state.result,
        onValueChange = {},
        label = { Text(stringResource(R.string.to_unit)) },
        isError = isError && activeField == 1,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { if (it.isFocused) onActiveFieldChange(1) },
        textStyle = TextStyle(fontSize = 24.sp),
        singleLine = true,
        readOnly = true // Use custom keyboard instead
    )
}

@Composable
private fun ConverterButtonGrid(
    onInput: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onToggleSign: () -> Unit,
    allowNegative: Boolean,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    val buttons: List<Pair<Int?, () -> Unit>> = listOf(
        R.string.btn_7 to { onInput("7") },
        R.string.btn_8 to { onInput("8") },
        R.string.btn_9 to { onInput("9") },
        R.string.btn_ac to onClear,
        R.string.btn_4 to { onInput("4") },
        R.string.btn_5 to { onInput("5") },
        R.string.btn_6 to { onInput("6") },
        R.string.btn_del to onDelete,
        R.string.btn_1 to { onInput("1") },
        R.string.btn_2 to { onInput("2") },
        R.string.btn_3 to { onInput("3") },
        (if (allowNegative) R.string.btn_toggle_sign else null) to onToggleSign,
        R.string.btn_00 to { onInput("00") },
        R.string.btn_0 to { onInput("0") },
        R.string.btn_dot to { onInput(".") }
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier,
        userScrollEnabled = isLandscape,
        verticalArrangement = Arrangement.Bottom
    ) {
        items(buttons) { (resId, action) ->
            if (resId == null) {
                Spacer(
                    modifier = Modifier
                        .padding(4.dp)
                        .aspectRatio(if (isLandscape) 1.6f else 1f)
                )
            } else {
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

@Preview(showBackground = true)
@Composable
fun ConverterContentPreview() {
    CalcProTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ConverterContent(
                state = ConverterState(
                    category = "Length",
                    fromUnit = "m",
                    toUnit = "km",
                    inputValue = "1000",
                    result = "1.0"
                ),
                isError = false,
                onCategoryChange = { _, _, _ -> },
                onSwapUnits = {},
                onFromUnitChange = {},
                onToUnitChange = {},
                onActiveFieldChange = {},
                onInput = {},
                onClear = {},
                onDelete = {},
                onToggleSign = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
fun ConverterLandscapePreview() {
    CalcProTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ConverterContent(
                state = ConverterState(
                    category = "Weight",
                    fromUnit = "kg",
                    toUnit = "g",
                    inputValue = "2.5",
                    result = "2500.0"
                ),
                isError = false,
                onCategoryChange = { _, _, _ -> },
                onSwapUnits = {},
                onFromUnitChange = {},
                onToUnitChange = {},
                onActiveFieldChange = {},
                onInput = {},
                onClear = {},
                onDelete = {},
                onToggleSign = {}
            )
        }
    }
}
