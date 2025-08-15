package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewDarkLight

@Composable
fun PlatoIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String = "",
    tint: Color = LocalContentColor.current
) {
    Icon(
        modifier = modifier.testTag(imageVector.name),
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint
    )
}

@Composable
fun PlatoIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String = "",
    tint: Color = LocalContentColor.current
) {
    Icon(
        modifier = modifier
            .testTag(painter.toString()),
        painter = painter,
        contentDescription = contentDescription,
        tint = tint
    )
}

@[Composable PreviewDarkLight]
private fun PlatoIconPreview() {
    HateItOrRateItTheme {
        PlatoIcon(
            imageVector = Icons.Filled.AccessTime
        )
    }
}
