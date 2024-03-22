package com.ashtar.bus.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun BackIconButton(navigateUp: () -> Unit) {
    IconButton(onClick = navigateUp) {
        Icon(
            Icons.Outlined.ArrowBackIos,
            contentDescription = "返回",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}