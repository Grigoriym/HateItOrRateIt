package com.grappim.hateitorrateit.uikit.widgets.text

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewMulti

@Composable
fun TextH4(
    text: String,
    modifier: Modifier = Modifier,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.h4,
        overflow = overflow,
        maxLines = maxLines,
        textAlign = TextAlign.Center
    )
}

@Composable
fun TextH5(
    text: String,
    modifier: Modifier = Modifier,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.h5,
        overflow = overflow,
        maxLines = maxLines
    )
}

@Composable
fun TextH6(
    text: String,
    modifier: Modifier = Modifier,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.h6,
        overflow = overflow,
        maxLines = maxLines
    )
}

@[Composable PreviewMulti]
private fun TextH4Preview() {
    HateItOrRateItTheme {
        TextH4(text = "Some Text")
    }
}

@[Composable PreviewMulti]
private fun TextH5Preview() {
    HateItOrRateItTheme {
        TextH5(text = "Some Text")
    }
}

@[Composable PreviewMulti]
private fun TextH6Preview() {
    HateItOrRateItTheme {
        TextH6(text = "Some Text")
    }
}
