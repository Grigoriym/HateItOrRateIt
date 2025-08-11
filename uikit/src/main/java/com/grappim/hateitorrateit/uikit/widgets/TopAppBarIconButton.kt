package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewDarkLight

@Composable
fun PlatoIconButton(icon: ImageVector, onButtonClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        modifier = modifier
            .size(50.dp)
            .testTag(icon.name),
        onClick = onButtonClick,
        shape = CircleShape
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(),
            imageVector = icon,
            contentDescription = "",
            tint = Color.Black
        )
    }
}

@[Composable PreviewDarkLight]
private fun PlatoIconButtonPreview() {
    HateItOrRateItTheme {
        PlatoIconButton(
            icon = Icons.Filled.Abc,
            onButtonClick = {}
        )
    }
}
