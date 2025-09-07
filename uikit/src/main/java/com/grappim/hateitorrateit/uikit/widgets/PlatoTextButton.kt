package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewDarkLight

@Composable
fun PlatoTextButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        modifier = modifier.testTag(text),
        onClick = onClick
    ) {
        Text(text = text)
    }
}

@[Composable PreviewDarkLight]
private fun PlatoTextButtonPreview() {
    HateItOrRateItTheme {
        PlatoTextButton(
            text = "Delete Image",
            onClick = {}
        )
    }
}
