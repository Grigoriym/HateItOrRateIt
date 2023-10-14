package com.grappim.ui.widgets.text

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun TextHeadlineLarge(
    modifier: Modifier = Modifier,
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.h4,
        overflow = overflow,
        maxLines = maxLines,
    )
}

@Composable
fun TextHTitleLarge(
    modifier: Modifier = Modifier,
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.h6,
        overflow = overflow,
        maxLines = maxLines,
    )
}
