package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale

@Composable
fun PlatoImage(
    modifier: Modifier,
    painter: Painter,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String = painter.toString()
) {
    Image(
        modifier = modifier,
        painter = painter,
        contentDescription = contentDescription,
        contentScale = contentScale
    )
}

@Composable
fun PlatoImage(
    modifier: Modifier,
    imageVector: ImageVector,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String = imageVector.name
) {
    Image(
        modifier = modifier,
        imageVector = imageVector,
        contentDescription = contentDescription,
        contentScale = contentScale
    )
}
