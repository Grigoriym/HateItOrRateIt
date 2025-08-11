package com.grappim.hateitorrateit.ui.screens.main

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
import com.grappim.hateitorrateit.utils.ui.NativeText
import com.grappim.hateitorrateit.utils.ui.navigation.safeClick

@Composable
fun MainNavHost(
    navController: NavHostController,
    showSnackbar: (NativeText) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HomeNavDestination
    ) {
        composable<HomeNavDestination> { navBackStackEntry ->
            HomeScreen(
                onProductClick = { productId: Long ->
                    navBackStackEntry.safeClick {
                        navController.navigateToDetails(productId)
                    }
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

        composable<DetailsNavDestination> { navBackStackEntry ->
            fun NavBackStackEntry.getIsFromEdit(defaultValue: Boolean = false): Boolean =
                this.savedStateHandle
                    .get<Boolean>(IS_FROM_EDIT)
                    ?: defaultValue

            val isFromEdit = navBackStackEntry.getIsFromEdit(false)

            DetailsRoute(
                goBack = {
                    navBackStackEntry.safeClick {
                        navController.popBackStack()
                    }
                },
                onImageClick = { productId, index ->
                    navBackStackEntry.safeClick {
                        navController.navigateToProductImage(productId, index)
                    }
                },
                onEditClick = { id: Long ->
                    navBackStackEntry.safeClick {
                        navController.navigateToProductManager(id)
                    }
                },
                isFromEdit = isFromEdit
            )
        }

        composable<ProductImageNavDestination> { navBackStackEntry ->
            ProductImageScreen(
                goBack = {
                    navBackStackEntry.safeClick {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable<SettingsNavDestination> { navBackStackEntry ->
            SettingsRoute()
        }
    }
}
