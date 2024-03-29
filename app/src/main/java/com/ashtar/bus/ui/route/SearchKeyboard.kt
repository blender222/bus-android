package com.ashtar.bus.ui.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchKeyboard(
    keyboardMore: Boolean,
    setKeyboardMore: (Boolean) -> Unit,
    getByReplace: (String) -> Unit,
    getByInput: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        if (keyboardMore) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KeyboardButton(text = "先導") { getByReplace("先導") }
                        KeyboardButton(text = "市民") { getByReplace("市民") }
                        KeyboardButton(text = "跳蛙") { getByReplace("跳蛙") }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KeyboardButton(text = "內科") { getByReplace("內科") }
                        KeyboardButton(text = "花季") { getByReplace("花季") }
                        KeyboardButton(text = "懷恩") { getByReplace("懷恩") }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KeyboardButton(text = "南軟") { getByReplace("南軟") }
                        KeyboardButton(text = "貓空") { getByReplace("貓空") }
                        KeyboardButton(text = "夜間") { getByReplace("夜") }
                    }
                }
                KeyboardButton(text = "返回") { setKeyboardMore(false) }
            }
        } else {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeyboardButton(text = "紅") { getByReplace("紅") }
                    KeyboardButton(text = "綠") { getByReplace("綠") }
                    KeyboardButton(text = "棕") { getByReplace("棕") }
                    KeyboardButton(text = "更多") { setKeyboardMore(true) }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeyboardButton(text = "藍") { getByReplace("藍") }
                    KeyboardButton(text = "橘") { getByReplace("橘") }
                    KeyboardButton(text = "小") { getByReplace("小") }
                    KeyboardButton(text = "幹線") { getByReplace("幹線") }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeyboardButton(text = "1") { getByInput("1") }
                    KeyboardButton(text = "4") { getByInput("4") }
                    KeyboardButton(text = "7") { getByInput("7") }
                    KeyboardButton(text = "F") { getByReplace("F") }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeyboardButton(text = "2") { getByInput("2") }
                    KeyboardButton(text = "5") { getByInput("5") }
                    KeyboardButton(text = "8") { getByInput("8") }
                    KeyboardButton(text = "0") { getByInput("0") }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeyboardButton(text = "3") { getByInput("3") }
                    KeyboardButton(text = "6") { getByInput("6") }
                    KeyboardButton(text = "9") { getByInput("9") }
                    KeyboardButton(text = "清空") { getByReplace("") }
                }
            }
        }
    }
}

@Composable
fun KeyboardButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp
        ),
        contentPadding = PaddingValues()
    ) {
        Text(
            text = text,
            fontSize = 20.sp
        )
    }
}