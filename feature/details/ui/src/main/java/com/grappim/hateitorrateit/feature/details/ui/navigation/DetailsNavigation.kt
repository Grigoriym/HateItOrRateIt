package com.grappim.hateitorrateit.feature.details.ui.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.grappim.hateitorrateit.core.navigation.NavDestinations
import com.grappim.hateitorrateit.feature.details.ui.DetailsRoute
import com.grappim.hateitorrateit.feature.details.ui.productimage.ProductImageScreen
import com.grappim.hateitorrateit.utils.ui.navigation.safeClick

fun NavGraphBuilder.detailsScreen(navController: NavController) {
    composable(
        route = NavDestinations.Details.route,
        arguments = listOf(
            navArgument(NavDestinations.Details.KEY) {
                type = NavType.LongType
            }
        )
    ) { navBackStackEntry ->
        fun NavBackStackEntry.getIsFromEdit(defaultValue: Boolean = false): Boolean {
            return this.savedStateHandle
                .get<Boolean>(NavDestinations.Details.IS_FROM_EDIT)
                ?: defaultValue
        }

        val isFromEdit = navBackStackEntry.getIsFromEdit(false)

        DetailsRoute(
            goBack = {
                navBackStackEntry.safeClick {
                    navController.popBackStack()
                }
            },
            onImageClicked = { productId, index ->
                navBackStackEntry.safeClick {
                    navController.navigate(
                        NavDestinations.DetailsImage.getRouteToNavigate(
                            productId = productId,
                            index = index
                        )
                    )
                }
            },
            onEditClicked = { id: Long ->
                navBackStackEntry.safeClick {
                    navController.navigate(
                        NavDestinations.ProductManager.getRouteToNavigate(
                            id.toString()
                        )
                    )
                }
            },
            isFromEdit = isFromEdit
        )
    }

    composable(
        route = NavDestinations.DetailsImage.route,
        arguments = listOf(
            navArgument(NavDestinations.DetailsImage.KEY_INDEX) {
                type = NavType.IntType
            }
        )
    ) { navBackStackEntry ->
        ProductImageScreen(
            goBack = {
                navBackStackEntry.safeClick {
                    navController.popBackStack()
                }
            }
        )
    }
}
