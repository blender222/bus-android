@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.ashtar.bus.ui.group_manage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.ashtar.bus.component.Blocking
import com.ashtar.bus.component.DeleteDialog
import com.ashtar.bus.component.GrayDivider
import com.ashtar.bus.component.TopBar
import com.ashtar.bus.model.Group
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyColumnState

@Composable
fun GroupManageScreen(
    navigateUp: () -> Unit,
    viewModel: GroupManageViewModel = hiltViewModel()
) {
    val groupList by viewModel.groupList.collectAsState()

    ScreenContent(
        blocking = viewModel.blocking,
        groupList = groupList,
        navigateUp = navigateUp,
        insertGroup = viewModel::insertGroup,
        updateGroup = viewModel::updateGroup,
        updateSort = viewModel::updateSort,
        deleteGroup = viewModel::deleteGroup
    )
}

@Composable
fun ScreenContent(
    blocking: Boolean,
    groupList: List<Group>,
    navigateUp: () -> Unit = {},
    insertGroup: (String) -> Unit = {},
    updateGroup: (Group) -> Unit = {},
    updateSort: (List<Group>) -> Unit = {},
    deleteGroup: (Group) -> Unit = {}
) {
    var openAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(title = "管理群組", navigateUp = navigateUp)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openAddDialog = true },
                shape = CircleShape
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "新增群組"
                )
            }
        }
    ) { padding ->
        if (groupList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.LibraryBooks,
                    contentDescription = null,
                    modifier = Modifier.size(108.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "點擊 + 新增群組",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            GroupList(
                groupList = groupList,
                updateGroup = updateGroup,
                updateSort = updateSort,
                deleteGroup = deleteGroup,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            )
        }
        if (openAddDialog) {
            AddDialog(
                close = { openAddDialog = false },
                confirm = insertGroup
            )
        }
        if (blocking) {
            Blocking(modifier = Modifier.padding(padding))
        }
    }
}

@Composable
fun GroupList(
    groupList: List<Group>,
    updateGroup: (Group) -> Unit,
    updateSort: (List<Group>) -> Unit,
    deleteGroup: (Group) -> Unit,
    modifier: Modifier = Modifier
) {
    var list by remember(groupList) { mutableStateOf(groupList) }
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
        items(list, key = { it.id }) { group ->
            var openDeleteDialog by remember { mutableStateOf(false) }
            var openUpdateDialog by remember { mutableStateOf(false) }

            ReorderableItem(reorderableLazyColumnState, group.id) {
                Column(Modifier.padding(start = 16.dp, top = 12.dp, end = 4.dp)) {
                    Text(
                        text = group.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { openDeleteDialog = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "刪除群組"
                            )
                        }
                        IconButton(onClick = { openUpdateDialog = true }) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "修改名稱"
                            )
                        }
                        IconButton(
                            onClick = {},
                            Modifier.draggableHandle(
                                onDragStopped = { updateSort(list) }
                            )
                        ) {
                            Icon(
                                Icons.Filled.Reorder,
                                contentDescription = "修改排序"
                            )
                        }
                    }
                    GrayDivider(Modifier.padding(end = 12.dp))
                }
            }
            if (openUpdateDialog) {
                UpdateDialog(
                    name = group.name,
                    close = { openUpdateDialog = false },
                    confirm = { updateGroup(group.copy(name = it)) }
                )
            }
            if (openDeleteDialog) {
                DeleteDialog(
                    text = "確定要刪除群組嗎?",
                    close = { openDeleteDialog = false },
                    confirm = { deleteGroup(group) }
                )
            }
        }
    }
}

@Composable
fun AddDialog(
    close: () -> Unit,
    confirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    Dialog(onDismissRequest = close) {
        Surface(shape = MaterialTheme.shapes.small) {
            Column {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        text = "新增群組",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(20.dp))
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("名稱") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            confirm(name)
                            close()
                        },
                        enabled = name.isNotEmpty(),
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text("確定")
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateDialog(
    name: String,
    close: () -> Unit,
    confirm: (String) -> Unit
) {
    var newName by remember { mutableStateOf(name) }

    Dialog(onDismissRequest = close) {
        Surface(shape = MaterialTheme.shapes.small) {
            Column {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        text = "修改群組名稱",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(20.dp))
                    TextField(
                        value = newName,
                        onValueChange = { newName = it },
                        placeholder = { Text("名稱") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = close,
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text("取消")
                    }
                    TextButton(
                        onClick = {
                            confirm(newName)
                            close()
                        },
                        enabled = newName.isNotEmpty(),
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text("確定")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun GroupManagePreview() {
    ScreenContent(
        blocking = false,
        groupList = listOf(
            Group(id = 1, sort = 1, name = "上班"),
            Group(id = 2, sort = 2, name = "下班"),
            Group(id = 3, sort = 3, name = "出去玩")
        )
    )
}