@file:OptIn(ExperimentalMaterialApi::class)

package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

const val PLAT_CARD_TAG = "plato_card_tag"

@Composable
fun PlatoCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    border: BorderStroke? = null,
    elevation: Dp = 6.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (onClick == null) {
        Card(
            modifier = modifier.testTag(PLAT_CARD_TAG),
            shape = shape,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            border = border,
            elevation = elevation,
            content = content
        )
    } else {
        Card(
            modifier = modifier.testTag(PLAT_CARD_TAG),
            shape = shape,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            border = border,
            elevation = elevation,
            onClick = onClick,
            content = content
        )
    }
}
