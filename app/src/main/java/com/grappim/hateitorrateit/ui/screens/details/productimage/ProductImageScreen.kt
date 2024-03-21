package com.grappim.hateitorrateit.ui.screens.details.productimage

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.grappim.hateitorrateit.ui.widgets.PlatoTopBar
import kotlin.math.max

private const val SCALE_COERCE_MAXIMUM_VALUE = 3f

@Composable
fun ProductImageScreen(viewModel: ProductImageViewModel = hiltViewModel(), goBack: () -> Unit) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    if (state.uri.isNotEmpty()) {
        ProductImageContent(
            state = state,
            goBack = goBack
        )
    }
}

@Composable
private fun ProductImageContent(state: ImageViewModelState, goBack: () -> Unit) {
    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Box {
            Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ImageContent(
                    uriString = state.uri
                )
            }
            PlatoTopBar(
                modifier = Modifier.padding(top = 2.dp),
                goBack = goBack,
                defaultBackButton = false,
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        }
    }
}

@Composable
private fun ImageContent(uriString: String) {
    var scale by remember {
        mutableFloatStateOf(1f)
    }
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }

    val painter = rememberAsyncImagePainter(uriString)

    Image(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    scale = scale.coerceIn(1f, SCALE_COERCE_MAXIMUM_VALUE)

                    val extraWidth = max(
                        0f,
                        (painter.intrinsicSize.width * scale - painter.intrinsicSize.width) / 2
                    )
                    val extraHeight = max(
                        0f,
                        (painter.intrinsicSize.height * scale - painter.intrinsicSize.height) / 2
                    )

                    offset = Offset(
                        x = (offset.x + pan.x * scale).coerceIn(-extraWidth, extraWidth),
                        y = (offset.y + pan.y * scale).coerceIn(-extraHeight, extraHeight)
                    )
                }
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offset.x
                translationY = offset.y
            },
        painter = painter,
        contentDescription = "",
        contentScale = ContentScale.Crop
    )
}
