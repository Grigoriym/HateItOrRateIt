package com.grappim.hateitorrateit.ui.screens.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.grappim.hateitorrateit.core.navigation.RootNavDestinations
import com.grappim.hateitorrateit.ui.screens.details.DetailsRoute
import com.grappim.hateitorrateit.ui.screens.details.productimage.ProductImageScreen
import com.grappim.hateitorrateit.ui.screens.rateorhate.HateOrRateRoute
import com.grappim.hateitorrateit.ui.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.utils.safeClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            HateItOrRateItTheme {
                MainScreen()
            }
        }
    }

    @Composable
    private fun MainScreen() {
        val navController = rememberNavController()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            NavHost(
                navController = navController,
                startDestination = RootNavDestinations.Home.route,
            ) {
                composable(RootNavDestinations.Home.route) { navBackStackEntry ->
                    RootMainScreen(
                        goToHateOrRate = {
                            navBackStackEntry.safeClick {
                                navController.navigate(RootNavDestinations.HateOrRate.route)
                            }
                        },
                        goToDetails = { id ->
                            navBackStackEntry.safeClick {
                                navController.navigate(
                                    RootNavDestinations.Details.getRouteWithProductId(
                                        id
                                    )
                                )
                            }
                        }
                    )
                }

                composable(RootNavDestinations.HateOrRate.route) { navBackStackEntry ->
                    HateOrRateRoute(
                        goBack = {
                            navBackStackEntry.safeClick {
                                navController.popBackStack()
                            }
                        },
                        onProductCreated = {
                            navBackStackEntry.safeClick {
                                navController.popBackStack()
                            }
                        }
                    )
                }
                composable(
                    route = RootNavDestinations.Details.route,
                    arguments = listOf(navArgument(RootNavDestinations.Details.KEY) {
                        type = NavType.LongType
                    })
                ) { navBackStackEntry ->
                    DetailsRoute(
                        goBack = {
                            navBackStackEntry.safeClick {
                                navController.popBackStack()
                            }
                        },
                        onImageClicked = { productId, index ->
                            navBackStackEntry.safeClick {
                                navController.navigate(
                                    RootNavDestinations.DetailsImage.getRouteWithUri(
                                        productId = productId,
                                        index = index
                                    )
                                )
                            }
                        }
                    )
                }
                composable(
                    route = RootNavDestinations.DetailsImage.route,
                    arguments = listOf(navArgument(RootNavDestinations.DetailsImage.KEY_INDEX) {
                        type = NavType.IntType
                    })
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
        }
    }
}
