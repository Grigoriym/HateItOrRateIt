package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

const val PROGRESS_INDICATOR_TAG = "progress_indicator_tag"

@Composable
fun PlatoProgressIndicator(show: Boolean, modifier: Modifier = Modifier) {
    if (show) {
        Box(modifier = modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(100.dp)
                    .testTag(PROGRESS_INDICATOR_TAG),
                strokeWidth = 6.dp
            )
        }
    }
}
