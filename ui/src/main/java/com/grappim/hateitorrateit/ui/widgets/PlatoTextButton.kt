package com.grappim.hateitorrateit.ui.widgets

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.grappim.hateitorrateit.ui.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.ui.utils.ThemePreviews

@Composable
fun PlatoTextButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier
            .testTag(text),
        onClick = onClick,
    ) {
        Text(text = text)
    }
}

@[Composable ThemePreviews]
private fun PlatoTextButtonPreview() {
    HateItOrRateItTheme {
        PlatoTextButton(
            text = "Delete Image",
            onClick = {}
        )
    }
}