package com.grappim.ui.widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PlatoIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    onButtonClick: () -> Unit
) {
    Button(
        modifier = modifier
            .size(50.dp),
        onClick = onButtonClick,
        shape = CircleShape
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize(),
            imageVector = icon,
            contentDescription = "",
            tint = Color.Black,
        )
    }
}