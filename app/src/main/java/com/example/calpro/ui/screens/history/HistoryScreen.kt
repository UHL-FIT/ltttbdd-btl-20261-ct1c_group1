package com.example.calpro.ui.screens.history

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calpro.CalcApplication
import com.example.calpro.R
import com.example.calpro.data.local.model.CalculationModel
import com.example.calpro.ui.components.HistoryItem
import com.example.calpro.ui.theme.CalcProTheme
import com.example.calpro.viewmodel.HistoryViewModel
import com.example.calpro.viewmodel.ViewModelFactory

// onItemClick callback khi người dùng nhấn vào 1 dòng lịch sử, trả về biểu thức.

@Composable
fun HistoryScreen(
    onItemClick: (String) -> Unit = {}
) {
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
    val viewModel: HistoryViewModel = viewModel(factory = factory)

    val history by viewModel.history.collectAsStateWithLifecycle()

    HistoryContent(
        history = history,
        onDeleteSelected = { items ->
            if (items.size == history.size) {
                viewModel.deleteAll()
            } else {
                items.forEach { viewModel.deleteItem(it) }
            }
        },
        onItemClick = onItemClick
    )
}

@Composable
fun HistoryContent(
    history: List<CalculationModel>,
    onDeleteSelected: (List<CalculationModel>) -> Unit,
    onItemClick: (String) -> Unit
) {
    // Danh sách ID các mục đã được check
    var selectedIds by rememberSaveable { mutableStateOf(emptySet<Int>()) }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    // Khi history thay đổi (xóa xong), loại bỏ các ID đã check mà không còn tồn tại
    LaunchedEffect(history) {
        val validIds = history.map { it.id }.toSet()
        selectedIds = selectedIds.intersect(validIds)
    }

    val allSelected = history.isNotEmpty() && selectedIds.size == history.size

    // Dialog xác nhận xóa hàng loạt
    if (showDialog) {
        val count = selectedIds.size
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.history_delete_selected)) },
            text = { Text(stringResource(R.string.history_confirm_delete_selected, count)) },
            confirmButton = {
                TextButton(onClick = {
                    val toDelete = history.filter { it.id in selectedIds }
                    onDeleteSelected(toDelete)
                    selectedIds = emptySet()
                    showDialog = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE
    val headerPadding = if (isLandscape) 8.dp else 12.dp

    Scaffold(
        floatingActionButton = {
            if (selectedIds.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(stringResource(R.string.history_delete_count, selectedIds.size))
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Thanh header: tổng phép tính + chọn tất cả
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = headerPadding, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bên trái: Checkbox "Chọn tất cả" + label
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (history.isNotEmpty()) {
                        Checkbox(
                            checked = allSelected,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    selectedIds = history.map { it.id }.toSet()
                                } else {
                                    selectedIds = emptySet()
                                }
                            }
                        )
                        Text(
                            text = stringResource(R.string.history_select_all),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Bên phải: số mục đã chọn (chỉ còn chữ, bỏ nút xóa)
                Text(
                    text = stringResource(R.string.stat_total, history.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Danh sách lịch sử
            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(R.string.history_empty),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(history, key = { it.id }) { item ->
                        HistoryItem(
                            model = item,
                            isSelected = item.id in selectedIds,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    selectedIds = selectedIds + item.id
                                } else {
                                    selectedIds = selectedIds - item.id
                                }
                            },
                            onClick = { onItemClick(item.expression) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryPreview() {
    CalcProTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            HistoryContent(
                history = listOf(
                    CalculationModel(1, "12 + 34", "46", System.currentTimeMillis()),
                    CalculationModel(2, "50 * 2", "100", System.currentTimeMillis() - 3600000)
                ),
                onDeleteSelected = {},
                onItemClick = {}
            )
        }
    }
}
