package com.ashtar.bus.component

import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun GrayDivider(modifier: Modifier = Modifier) {
    Divider(
        modifier = modifier,
        color = Color(0xFFE3E3E3)
    )
}