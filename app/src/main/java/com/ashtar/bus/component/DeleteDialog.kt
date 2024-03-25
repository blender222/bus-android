package com.ashtar.bus.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DeleteDialog(
    text: String,
    close: () -> Unit,
    confirm: () -> Unit
) {
    Dialog(onDismissRequest = close) {
        Surface(shape = MaterialTheme.shapes.small) {
            Column {
                Text(
                    text = text,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(16.dp))
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
                            confirm()
                            close()
                        },
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text("確定")
                    }
                }
            }
        }
    }
}