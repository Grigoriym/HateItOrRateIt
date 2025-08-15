package com.grappim.hateitorrateit.core.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.grappim.hateitorrateit.feature.home.ui.navigation.HomeNavDestination
import com.grappim.hateitorrateit.feature.settings.ui.navigation.SettingsNavDestination
import com.grappim.hateitorrateit.strings.RString
import com.grappim.hateitorrateit.uikit.icons.PlatoIconType

fun BottomNavDestination.navigate(navController: NavHostController, navOptions: NavOptions) {
    navController.navigate(route = route, navOptions = navOptions)
}

enum class BottomNavDestination(val route: Any, @StringRes val label: Int, val icon: ImageVector) {
    HOME(
        route = HomeNavDestination,
        label = RString.home,
        icon = PlatoIconType.Home.imageVector
    ),
    SETTINGS(
        route = SettingsNavDestination,
        label = RString.settings,
        icon = PlatoIconType.Settings.imageVector
    )
}
