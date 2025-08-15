package com.grappim.hateitorrateit.ui.widgets

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.grappim.hateitorrateit.core.navigation.BottomNavDestination
import com.grappim.hateitorrateit.uikit.widgets.PlatoIcon

@Composable
fun BottomBar(
    screens: List<BottomNavDestination>,
    currentBottomNavScreen: BottomNavDestination?,
    modifier: Modifier = Modifier,
    onBottomNavItemClick: (BottomNavDestination) -> Unit = {}
) {
    NavigationBar(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp
                )
            )
    ) {
        screens.forEach { screen ->
            NavigationBarItem(
                selected = currentBottomNavScreen == screen,
                label = {
                    Text(text = stringResource(screen.label))
                },
                onClick = {
                    onBottomNavItemClick(screen)
                },
                icon = { PlatoIcon(imageVector = screen.icon) }
            )
        }
    }
}
