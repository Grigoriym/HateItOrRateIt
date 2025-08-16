package com.grappim.hateitorrateit.ui.screens.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.grappim.hateitorrateit.feature.details.ui.DetailsRoute
import com.grappim.hateitorrateit.feature.details.ui.navigation.DetailsNavDestination
import com.grappim.hateitorrateit.feature.details.ui.navigation.IS_FROM_EDIT
import com.grappim.hateitorrateit.feature.details.ui.navigation.ProductImageNavDestination
import com.grappim.hateitorrateit.feature.details.ui.navigation.navigateToDetails
import com.grappim.hateitorrateit.feature.details.ui.navigation.navigateToProductImage
import com.grappim.hateitorrateit.feature.details.ui.productimage.ProductImageScreen
import com.grappim.hateitorrateit.feature.home.ui.HomeScreen
import com.grappim.hateitorrateit.feature.home.ui.navigation.HomeNavDestination
import com.grappim.hateitorrateit.feature.productmanager.ui.ProductManagerRoute
import com.grappim.hateitorrateit.feature.productmanager.ui.navigation.ProductManagerNavDestination
import com.grappim.hateitorrateit.feature.productmanager.ui.navigation.navigateToProductManager
import com.grappim.hateitorrateit.feature.settings.ui.navigation.SettingsNavDestination
import com.grappim.hateitorrateit.feature.settings.ui.screen.SettingsRoute
import com.grappim.hateitorrateit.feature.settings.ui.screen.about.SettingsAboutScreenRoute
import com.grappim.hateitorrateit.feature.settings.ui.screen.about.SettingsAboutScreenRouteNavDestination
import com.grappim.hateitorrateit.feature.settings.ui.screen.about.goToSettingsAboutScreen
import com.grappim.hateitorrateit.feature.settings.ui.screen.analytics.SettingsAnalyticsScreenRoute
import com.grappim.hateitorrateit.feature.settings.ui.screen.analytics.SettingsAnalyticsScreenRouteNavDestination
import com.grappim.hateitorrateit.feature.settings.ui.screen.analytics.goToSettingsAnalyticsScreen
import com.grappim.hateitorrateit.feature.settings.ui.screen.database.SettingsDatabaseScreenRoute
import com.grappim.hateitorrateit.feature.settings.ui.screen.database.SettingsDatabaseScreenRouteNavDestination
import com.grappim.hateitorrateit.feature.settings.ui.screen.database.goToSettingsDatabaseScreen
import com.grappim.hateitorrateit.feature.settings.ui.screen.interfacescreen.SettingsInterfaceScreenRoute
import com.grappim.hateitorrateit.feature.settings.ui.screen.interfacescreen.SettingsInterfaceScreenRouteNavDestination
import com.grappim.hateitorrateit.feature.settings.ui.screen.interfacescreen.goToSettingsInterfaceScreen
import com.grappim.hateitorrateit.feature.settings.ui.screen.product.SettingsProductScreenRoute
import com.grappim.hateitorrateit.feature.settings.ui.screen.product.SettingsProductScreenRouteNavDestination
import com.grappim.hateitorrateit.feature.settings.ui.screen.product.goToSettingsProductScreen
import com.grappim.hateitorrateit.utils.ui.NativeText

@Composable
fun MainNavHost(
    navController: NavHostController,
    showActionSnackbar: (NativeText, actionLabel: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HomeNavDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(100))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(100))
        }
    ) {
        composable<HomeNavDestination> { navBackStackEntry ->
            HomeScreen(
                onProductClick = { productId: Long ->
                    navController.navigateToDetails(productId)
                }
            )
        }

        composable<ProductManagerNavDestination> { navBackStackEntry ->
            fun handleBackNavigation(isNewProduct: Boolean) {
                if (!isNewProduct) {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(IS_FROM_EDIT, true)
                }
                navController.popBackStack()
            }

            ProductManagerRoute(
                goBack = { isNewProduct: Boolean ->
                    handleBackNavigation(isNewProduct)
                },
                onProductFinish = { isNewProduct: Boolean ->
                    handleBackNavigation(isNewProduct)
                },
                showActionSnackbar = showActionSnackbar
            )
        }

        composable<DetailsNavDestination> { navBackStackEntry ->
            fun NavBackStackEntry.getIsFromEdit(defaultValue: Boolean = false): Boolean =
                this.savedStateHandle
                    .get<Boolean>(IS_FROM_EDIT)
                    ?: defaultValue

            val isFromEdit = navBackStackEntry.getIsFromEdit(false)

            DetailsRoute(
                goBack = {
                    navController.popBackStack()
                },
                onImageClick = { productId, index ->
                    navController.navigateToProductImage(productId, index)
                },
                onEditClick = { id: Long ->
                    navController.navigateToProductManager(id)
                },
                isFromEdit = isFromEdit,
                showSnackbar = showActionSnackbar
            )
        }

        composable<ProductImageNavDestination> { navBackStackEntry ->
            ProductImageScreen(
                goBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<SettingsNavDestination> { navBackStackEntry ->
            SettingsRoute(
                goToAboutScreen = {
                    navController.goToSettingsAboutScreen()
                },
                goToInterfaceScreen = {
                    navController.goToSettingsInterfaceScreen()
                },
                goToDatabaseScreen = {
                    navController.goToSettingsDatabaseScreen()
                },
                goToAnalyticsScreen = {
                    navController.goToSettingsAnalyticsScreen()
                },
                goToProductScreen = {
                    navController.goToSettingsProductScreen()
                }
            )
        }

        composable<SettingsAboutScreenRouteNavDestination> {
            SettingsAboutScreenRoute()
        }
        composable<SettingsInterfaceScreenRouteNavDestination> {
            SettingsInterfaceScreenRoute()
        }
        composable<SettingsDatabaseScreenRouteNavDestination> {
            SettingsDatabaseScreenRoute()
        }
        composable<SettingsAnalyticsScreenRouteNavDestination> {
            SettingsAnalyticsScreenRoute()
        }
        composable<SettingsProductScreenRouteNavDestination> {
            SettingsProductScreenRoute()
        }
    }
}
