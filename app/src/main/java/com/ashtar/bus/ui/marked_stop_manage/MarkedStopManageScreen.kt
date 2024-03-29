@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.ashtar.bus.ui.marked_stop_manage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ashtar.bus.common.DataProvider
import com.ashtar.bus.component.Blocking
import com.ashtar.bus.component.DeleteDialog
import com.ashtar.bus.component.GrayDivider
import com.ashtar.bus.component.MarkedStopText
import com.ashtar.bus.component.SearchPlaceholder
import com.ashtar.bus.component.TopBar
import com.ashtar.bus.model.MarkedStop
import com.ashtar.bus.ui.theme.BusTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyColumnState

@Composable
fun MarkedStopManageScreen(
    navigateUp: () -> Unit,
    toRoute: () -> Unit,
    viewModel: MarkedStopManageViewModel = hiltViewModel()
) {
    val stopList by viewModel.stopList.collectAsState()

    ScreenContent(
        blocking = viewModel.blocking,
        stopList = stopList,
        navigateUp = navigateUp,
        toRoute = toRoute,
        updateSort = viewModel::updateSort,
        deleteMarkedStop = viewModel::deleteMarkedStop
    )
}

@Composable
fun ScreenContent(
    blocking: Boolean,
    stopList: List<MarkedStop>,
    navigateUp: () -> Unit = {},
    toRoute: () -> Unit = {},
    updateSort: (List<MarkedStop>) -> Unit = {},
    deleteMarkedStop: (Int) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopBar(title = "編輯群組站牌", navigateUp = navigateUp)
        }
    ) { padding ->
        if (stopList.isEmpty()) {
            SearchPlaceholder(toRoute)
        } else {
            MarkedStopList(
                stopList = stopList,
                updateSort = updateSort,
                deleteMarkedStop = deleteMarkedStop,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            )
        }
        if (blocking) {
            Blocking(modifier = Modifier.padding(padding))
        }
    }
}

@Composable
fun MarkedStopList(
    stopList: List<MarkedStop>,
    updateSort: (List<MarkedStop>) -> Unit,
    deleteMarkedStop: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var list by remember(stopList) { mutableStateOf(stopList) }
    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState = rememberReorderableLazyColumnState(lazyListState) { from, to ->
        list = list.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        items(list, key = { it.id }) { item: MarkedStop ->
            var openDeleteDialog by remember { mutableStateOf(false) }

            ReorderableItem(reorderableLazyColumnState, item.id) {
                Column(Modifier.padding(start = 16.dp, end = 4.dp)) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MarkedStopText(
                            item = item,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { openDeleteDialog = true }
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "刪除站牌",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                        IconButton(
                            onClick = {},
                            modifier = Modifier.draggableHandle(
                                onDragStopped = { updateSort(list) }
                            )
                        ) {
                            Icon(
                                Icons.Filled.Reorder,
                                contentDescription = "修改排序",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    GrayDivider(Modifier.padding(end = 12.dp))
                }
            }
            if (openDeleteDialog) {
                DeleteDialog(
                    text = "確定要刪除此站牌嗎?",
                    close = { openDeleteDialog = false },
                    confirm = { deleteMarkedStop(item.id) }
                )
            }
        }
    }
}

@Preview
@Composable
fun MarkedStopManagePreview() {
    BusTheme {
        ScreenContent(
            blocking = false,
            stopList = DataProvider.markedStopList
        )
    }
}