package com.grappim.ui.widgets

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun PlatoIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    onButtonClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
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