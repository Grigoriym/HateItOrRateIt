package com.grappim.hateitorrateit.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlatoPagerIndicator(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.surface.copy(
        alpha = 0.2f
    ),
    show: Boolean,
    size: Int,
    pagerState: PagerState,
) {
    if (show) {
        PlatoCard(
            modifier = modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .padding(bottom = 16.dp),
            backgroundColor = backgroundColor
        ) {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(size) { iteration ->
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
