package com.grappim.hateitorrateit.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface HomeNavDestination {
    val route: String
    val title: String
    val imageVector: ImageVector

    data object Home : HomeNavDestination {
        override val route: String = "home"
        override val title: String = "Home"
        override val imageVector: ImageVector = Icons.Filled.Home
    }

    data object Settings : HomeNavDestination {
        override val route: String = "settings"
        override val title: String = "Settings"
        override val imageVector: ImageVector = Icons.Filled.Settings
    }

}