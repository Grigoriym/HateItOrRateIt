package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewDarkLight

@Composable
fun PlatoIconButton(icon: ImageVector, onButtonClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .testTag(icon.name),
        onClick = onButtonClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@[Composable PreviewDarkLight]
private fun PlatoIconButtonPreview() {
    HateItOrRateItTheme {
        PlatoIconButton(
            icon = Icons.Filled.Camera,
            onButtonClick = {}
        )
    }
}
