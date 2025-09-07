package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

const val PLATO_CARD_TAG = "plato_card_tag"

@Composable
fun PlatoCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    border: BorderStroke? = null,
    elevation: Dp = 6.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val defaultBorder = border ?: if (isSystemInDarkTheme()) {
        BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    } else {
        null
    }

    if (onClick == null) {
        Card(
            modifier = modifier.testTag(PLATO_CARD_TAG),
            shape = shape,
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevation
            ),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor,
                contentColor = contentColor
            ),
            border = defaultBorder,
            content = content
        )
    } else {
        Card(
            modifier = modifier.testTag(PLATO_CARD_TAG),
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor,
                contentColor = contentColor
            ),
            border = defaultBorder,
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevation
            ),
            onClick = onClick,
            content = content
        )
    }
}
