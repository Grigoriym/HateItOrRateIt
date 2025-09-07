package com.grappim.hateitorrateit.uikit.widgets.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewDarkLight

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
        style = MaterialTheme.typography.headlineMedium,
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
        style = MaterialTheme.typography.headlineSmall,
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
        style = MaterialTheme.typography.titleLarge,
        overflow = overflow,
        maxLines = maxLines
    )
}

@[Composable PreviewDarkLight]
private fun TextH4Preview() {
    HateItOrRateItTheme {
        TextH4(text = "Some Text")
    }
}

@[Composable PreviewDarkLight]
private fun TextH5Preview() {
    HateItOrRateItTheme {
        TextH5(text = "Some Text")
    }
}

@[Composable PreviewDarkLight]
private fun TextH6Preview() {
    HateItOrRateItTheme {
        TextH6(text = "Some Text")
    }
}
