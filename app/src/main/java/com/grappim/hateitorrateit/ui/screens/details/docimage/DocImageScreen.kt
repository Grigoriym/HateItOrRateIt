package com.grappim.hateitorrateit.ui.screens.details.docimage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.grappim.ui.widgets.PlatoCard
import com.grappim.ui.widgets.PlatoIconButton
import com.grappim.ui.widgets.PlatoTopBar

@Composable
fun DocImageScreen(
    viewModel: DocImageViewModel = hiltViewModel(),
    goBack: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    if (state.uri.isNotEmpty()) {
        DocImageContent(
            state = state,
            goBack = goBack,
        )
    }
}

@Composable
private fun DocImageContent(
    state: DocImageViewModelState,
    goBack: () -> Unit
) {
    val pagerState = rememberPagerState {
        state.fileUris.size
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
                val file = state.fileUris[page]
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

            if (state.fileUris.size > 1) {
                PlatoCard(
                    modifier = Modifier
                        .wrapContentHeight()
                        .wrapContentWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    backgroundColor = MaterialTheme.colors.surface.copy(
                        alpha = 0.2f
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(state.fileUris.size) { iteration ->
                            val color =
                                if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                            Box(
                                modifier = Modifier
                                    .padding(
                                        horizontal = 4.dp,
                                        vertical = 6.dp
                                    )
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
