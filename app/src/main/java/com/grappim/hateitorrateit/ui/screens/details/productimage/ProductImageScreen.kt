package com.grappim.hateitorrateit.ui.screens.details.productimage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.grappim.hateitorrateit.ui.widgets.PlatoPagerIndicator
import com.grappim.hateitorrateit.ui.widgets.PlatoTopBar

@Composable
fun ProductImageScreen(
    viewModel: ProductImageViewModel = hiltViewModel(),
    goBack: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    if (state.uri.isNotEmpty()) {
        ImageContent(
            state = state,
            goBack = goBack,
        )
    }
}

@Composable
private fun ImageContent(
    state: ImageViewModelState,
    goBack: () -> Unit
) {
    val pagerState = rememberPagerState {
        state.images.size
    }

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        ) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                state = pagerState
            ) { page ->
                val file = state.images[page]
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = rememberAsyncImagePainter(file.uriString),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }

            PlatoTopBar(
                modifier = Modifier.padding(top = 2.dp),
                goBack = goBack,
                defaultBackButton = false,
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
            )

            PlatoPagerIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                show = state.images.size > 1,
                size = state.images.size,
                pagerState = pagerState,
            )
        }
    }
}
