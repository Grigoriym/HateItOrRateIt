package com.grappim.hateitorrateit.core.navigation

sealed interface HomeNavDestination {
    val route: String
    val title: String

    data object Home : HomeNavDestination {
        override val route: String = "home"
        override val title: String = "Home"
    }

    data object Settings : HomeNavDestination {
        override val route: String = "settings"
        override val title: String = "Settings"
    }

}