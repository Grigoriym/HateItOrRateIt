package com.grappim.hateitorrateit.feature.productmanager.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.grappim.hateitorrateit.core.navigation.NavDestinations
import com.grappim.hateitorrateit.feature.productmanager.ui.ProductManagerRoute
import com.grappim.hateitorrateit.utils.ui.navigation.safeClick

fun NavGraphBuilder.productManagerScreen(navController: NavController) {
    /**
     * We pass productId as String because Long cannot be nullable in safeArgs
     */
    composable(
        route = NavDestinations.ProductManager.route,
        arguments = listOf(
            navArgument(NavDestinations.ProductManager.KEY_EDIT_PRODUCT_ID) {
                type = NavType.StringType
                nullable = true
            }
        )
    ) { navBackStackEntry ->
        fun handleBackNavigation(isNewProduct: Boolean) {
            if (!isNewProduct) {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(NavDestinations.Details.IS_FROM_EDIT, true)
            }
            navController.popBackStack()
        }

        ProductManagerRoute(
            goBack = { isNewProduct: Boolean ->
                navBackStackEntry.safeClick {
                    handleBackNavigation(isNewProduct)
                }
            },
            onProductFinish = { isNewProduct: Boolean ->
                navBackStackEntry.safeClick {
                    handleBackNavigation(isNewProduct)
                }
            }
        )
    }
}
