package com.ashtar.bus.ui.stop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ashtar.bus.model.Group
import com.ashtar.bus.model.Stop

@Composable
fun MenuDialog(
    stop: Stop,
    openAddToGroupDialog: () -> Unit,
    closeDialog: () -> Unit
) {
    Dialog(onDismissRequest = closeDialog) {
        Surface(shape = MaterialTheme.shapes.small) {
            Column {
                Text(
                    text = stop.name,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Divider(color = Color.LightGray)
                Column {
                    Row(
                        modifier = Modifier
                            .clickable(onClick = openAddToGroupDialog)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.BookmarkAdd,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color(0xFF666666)
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = "加入常用站牌",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddToGroupDialog(
    stop: Stop,
    groupList: List<Group>,
    openNewGroupDialog: () -> Unit,
    closeDialog: () -> Unit,
    insertMarkedStop: (Group, Stop) -> Unit
) {
    var selected: Group? by remember { mutableStateOf(null) }

    Dialog(onDismissRequest = closeDialog) {
        Surface(shape = MaterialTheme.shapes.small) {
            Column {
                Text(
                    text = "加入至哪個群組？",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                LazyColumn {
                    items(groupList) { group ->
                        Row(
                            modifier = Modifier
                                .clickable { selected = group }
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = group.name,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            RadioButton(
                                selected = group == selected,
                                onClick = null
                            )
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .clickable(onClick = openNewGroupDialog)
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "建立新群組",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = closeDialog,
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text("取消")
                    }
                    TextButton(
                        onClick = {
                            insertMarkedStop(selected!!, stop)
                            closeDialog()
                        },
                        enabled = selected != null,
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
fun NewGroupDialog(
    stop: Stop,
    closeDialog: () -> Unit,
    insertGroupWithMarkedStop: (String, Stop) -> Unit
) {
    var name by remember { mutableStateOf("") }

    Dialog(onDismissRequest = closeDialog) {
        Surface(shape = MaterialTheme.shapes.small) {
            Column {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        text = "建立新群組",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(8.dp))
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
                        onClick = closeDialog,
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text("取消")
                    }
                    TextButton(
                        onClick = {
                            insertGroupWithMarkedStop(name, stop)
                            closeDialog()
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