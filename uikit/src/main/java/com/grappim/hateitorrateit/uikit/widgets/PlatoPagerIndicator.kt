@file:OptIn(ExperimentalFoundationApi::class)

package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewDarkLight

@Composable
fun PlatoPagerIndicator(
    show: Boolean,
    size: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface
) {
    if (show) {
        PlatoCard(
            modifier = modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .padding(bottom = 16.dp),
            backgroundColor = backgroundColor,
            elevation = 0.dp
        ) {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(size) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) {
                            Color.DarkGray
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
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

@[Composable PreviewDarkLight]
private fun PlatoPagerIndicatorPreview() {
    val pagerState = rememberPagerState {
        5
    }
    HateItOrRateItTheme {
        PlatoPagerIndicator(
            show = true,
            pagerState = pagerState,
            size = 5
        )
    }
}
