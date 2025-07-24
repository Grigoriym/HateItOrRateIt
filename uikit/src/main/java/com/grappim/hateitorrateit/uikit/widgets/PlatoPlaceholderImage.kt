package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.grappim.hateitorrateit.uikit.icons.PlatoIconType
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewMulti

const val PLATO_PLACEHOLDER_IMAGE_TAG = "plato_placeholder_image_tag"

@Composable
fun PlatoPlaceholderImage(
    modifier: Modifier = Modifier,
    imageVector: ImageVector = PlatoIconType.ImageNotSupported.imageVector
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag(PLATO_PLACEHOLDER_IMAGE_TAG),
        contentAlignment = Alignment.Center
    ) {
        PlatoImage(
            modifier = Modifier
                .padding(all = 52.dp)
                .fillMaxSize(),
            imageVector = imageVector
        )
    }
}

@[Composable PreviewMulti]
private fun PlatoPlaceholderImagePreview() {
    HateItOrRateItTheme {
        PlatoPlaceholderImage()
    }
}
