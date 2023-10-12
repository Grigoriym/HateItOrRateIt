package com.grappim.ui.widgets

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun TopAppBarIconButton(
    icon: ImageVector,
    onButtonClick: () -> Unit
) {
    IconButton(
        onClick = onButtonClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.White
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
            tint = Color.Black,
        )
    }
}