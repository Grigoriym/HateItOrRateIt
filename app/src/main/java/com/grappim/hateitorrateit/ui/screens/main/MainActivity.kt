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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.grappim.hateitorrateit.core.navigation.RootNavDestinations
import com.grappim.hateitorrateit.ui.screens.details.DetailsScreen
import com.grappim.hateitorrateit.ui.screens.details.docimage.DocImageScreen
import com.grappim.hateitorrateit.ui.screens.rateorhate.RateOrHateScreen
import com.grappim.ui.theme.HateItOrRateItTheme
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
    private fun MainScreen(
        rootViewModel: MainActivityViewModel = hiltViewModel()
    ) {
        val navController = rememberNavController()
        rootViewModel.setupNavController(navController)

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            NavHost(
                navController = navController,
                startDestination = RootNavDestinations.Home.route,
            ) {
                composable(RootNavDestinations.Home.route) {
                    RootMainScreen(
                        rootViewModel = rootViewModel,
                        goToHateOrRate = {
                            navController.navigate(RootNavDestinations.HateOrRate.route)
                        },
                        goToDetails = { id ->
                            navController.navigate(RootNavDestinations.Details.getRouteWithDocId(id))
                        }
                    )
                }

                composable(RootNavDestinations.HateOrRate.route) {
                    RateOrHateScreen(
                        goBack = {
                            navController.popBackStack()
                        },
                        onDocumentCreated = {
                            navController.popBackStack()
                        }
                    )
                }
                composable(
                    route = RootNavDestinations.Details.route,
                    arguments = listOf(navArgument(RootNavDestinations.Details.KEY) {
                        type = NavType.LongType
                    })
                ) {
                    DetailsScreen(
                        goBack = {
                            navController.popBackStack()
                        },
                        onDocImageClicked = {
                            navController.navigate(
                                RootNavDestinations.DetailsImage.getRouteWithUri(
                                    it
                                )
                            )
                        }
                    )
                }
                composable(
                    route = RootNavDestinations.DetailsImage.route,
                    arguments = listOf(navArgument(RootNavDestinations.DetailsImage.KEY) {
                        type = NavType.StringType
                    })
                ) {
                    DocImageScreen(
                        goBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}