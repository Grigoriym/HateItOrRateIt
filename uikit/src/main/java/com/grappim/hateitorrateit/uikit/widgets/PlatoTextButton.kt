package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewMulti

@Composable
fun PlatoTextButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        modifier = modifier.testTag(text),
        onClick = onClick
    ) {
        Text(text = text)
    }
}

@[Composable PreviewMulti]
private fun PlatoTextButtonPreview() {
    HateItOrRateItTheme {
        PlatoTextButton(
            text = "Delete Image",
            onClick = {}
        )
    }
}
