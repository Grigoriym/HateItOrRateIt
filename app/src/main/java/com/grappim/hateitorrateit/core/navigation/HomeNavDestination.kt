package com.grappim.hateitorrateit.core.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.grappim.hateitorrateit.ui.utils.PlatoIconType

sealed interface HomeNavDestination {
    val route: String
    val title: String
    val imageVector: ImageVector

    data object Home : HomeNavDestination {
        override val route: String = "home"
        override val title: String = "Home"
        override val imageVector: ImageVector = PlatoIconType.Home.imageVector
    }

    data object Settings : HomeNavDestination {
        override val route: String = "settings"
        override val title: String = "Settings"
        override val imageVector: ImageVector = PlatoIconType.Settings.imageVector
    }
}
