package com.grappim.hateitorrateit.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.grappim.hateitorrateit.ui.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.ui.utils.PlatoIconType
import com.grappim.hateitorrateit.ui.utils.ThemePreviews

const val PLATO_PLACEHOLDER_IMAGE_TAG = "plato_placeholder_image_tag"

@Composable
fun PlatoPlaceholderImage(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag(PLATO_PLACEHOLDER_IMAGE_TAG),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            imageVector = PlatoIconType.ImageNotSupported.imageVector,
            contentDescription = "",
            modifier = Modifier
                .padding(all = 52.dp)
                .fillMaxSize()
        )
    }
}

@[Composable ThemePreviews]
private fun PlatoPlaceholderImagePreview() {
    HateItOrRateItTheme {
        PlatoPlaceholderImage()
    }
}
