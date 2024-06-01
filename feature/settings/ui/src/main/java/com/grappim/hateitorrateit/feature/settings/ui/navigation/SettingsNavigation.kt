package com.grappim.hateitorrateit.feature.settings.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.grappim.hateitorrateit.core.navigation.BottomNavDestinations
import com.grappim.hateitorrateit.feature.settings.ui.screen.SettingsRoute
import com.grappim.hateitorrateit.utils.ui.PlatoIconType
import com.grappim.hateitorrateit.utils.ui.navigation.safeClick

fun NavGraphBuilder.settingsScreen(navController: NavController) {
    composable(SettingsNavScreen.route) { navBackStackEntry ->
        SettingsRoute(
            goBack = {
                navBackStackEntry.safeClick {
                    navController.popBackStack()
                }
            }
        )
    }
}

data object SettingsNavScreen : BottomNavDestinations {
    override val route: String = "settings"
    override val title: String = "Settings"
    override val imageVector: ImageVector = PlatoIconType.Settings.imageVector
}
