package com.grappim.hateitorrateit.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

const val PLATO_LOADING_DIALOG_TAG = "plato_loading_dialog_tag"

@Composable
fun PlatoLoadingDialog(isLoading: Boolean) {
    if (isLoading) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(PLATO_LOADING_DIALOG_TAG),
                shape = RoundedCornerShape(16.dp),
                color = Color.Transparent,
            ) {
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize(0.3f),
                        strokeWidth = 8.dp,
                        color = Color.Blue,
                    )
                }
            }
        }
    }
}
