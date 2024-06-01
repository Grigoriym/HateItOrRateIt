package com.grappim.hateitorrateit.feature.home.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.grappim.hateitorrateit.core.navigation.BottomNavDestinations
import com.grappim.hateitorrateit.feature.home.ui.HomeScreen
import com.grappim.hateitorrateit.utils.ui.PlatoIconType
import com.grappim.hateitorrateit.utils.ui.navigation.safeClick

fun NavGraphBuilder.homeScreen(goToDetails: (id: Long) -> Unit) {
    composable(HomeNavScreen.route) { navBackStackEntry ->
        HomeScreen(
            onProductClick = { productId: Long ->
                navBackStackEntry.safeClick {
                    goToDetails(productId)
                }
            }
        )
    }
}

data object HomeNavScreen : BottomNavDestinations {
    override val route: String = "home"
    override val title: String = "Home"
    override val imageVector: ImageVector = PlatoIconType.Home.imageVector
}
