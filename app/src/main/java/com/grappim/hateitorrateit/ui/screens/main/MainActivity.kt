package com.grappim.hateitorrateit.ui.screens.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.grappim.hateitorrateit.core.navigation.RootNavDestinations
import com.grappim.hateitorrateit.domain.DarkThemeConfig
import com.grappim.hateitorrateit.ui.screens.details.DetailsRoute
import com.grappim.hateitorrateit.ui.screens.details.productimage.ProductImageScreen
import com.grappim.hateitorrateit.ui.screens.productmanager.ProductManagerRoute
import com.grappim.hateitorrateit.ui.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.utils.safeClick
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import com.grappim.hateitorrateit.ui.R as uikit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        checkForAppUpdates()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val state by viewModel.viewState.collectAsStateWithLifecycle()
            val darkTheme = shouldUseDarkTheme(mainActivityViewState = state)
            HateItOrRateItTheme(
                darkTheme = darkTheme
            ) {
                MainScreen(darkTheme)
            }
        }
    }

    private fun checkForAppUpdates() {
        val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            Timber.d("appUpdateInfo: $info")
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            if (info.isFlexibleUpdateAllowed && isUpdateAvailable) {
                val type = AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    this,
                    type,
                    0
                )
            }
            if (info.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate(appUpdateManager)
            }
        }.addOnFailureListener {
            Timber.e(it, "appUpdateInfo failure")
        }
    }

    private fun popupSnackbarForCompleteUpdate(appUpdateManager: AppUpdateManager) {
        Snackbar.make(
            window.decorView.rootView,
            getString(uikit.string.app_update_downloaded),
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(getString(uikit.string.restart)) { appUpdateManager.completeUpdate() }
            show()
        }
    }

    @Composable
    private fun MainScreen(darkTheme: Boolean) {
        val navController = rememberNavController()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            NavHost(
                navController = navController,
                startDestination = RootNavDestinations.Home.route
            ) {
                composable(RootNavDestinations.Home.route) { navBackStackEntry ->
                    RootMainScreen(
                        darkTheme = darkTheme,
                        goToHateOrRate = {
                            navBackStackEntry.safeClick {
                                navController.navigate(
                                    RootNavDestinations.ProductManager.getRouteToNavigate("")
                                )
                            }
                        },
                        goToDetails = { id ->
                            navBackStackEntry.safeClick {
                                navController.navigate(
                                    RootNavDestinations.Details.getRouteToNavigate(
                                        id
                                    )
                                )
                            }
                        }
                    )
                }

                /**
                 * We pass productId as String because Long cannot be nullable in safeArgs
                 */
                composable(
                    route = RootNavDestinations.ProductManager.route,
                    arguments = listOf(
                        navArgument(RootNavDestinations.ProductManager.KEY_EDIT_PRODUCT_ID) {
                            type = NavType.StringType
                            nullable = true
                        }
                    )
                ) { navBackStackEntry ->
                    fun handleBackNavigation(isNewProduct: Boolean) {
                        if (!isNewProduct) {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set(RootNavDestinations.Details.IS_FROM_EDIT, true)
                        }
                        navController.popBackStack()
                    }

                    ProductManagerRoute(
                        goBack = { isNewProduct: Boolean ->
                            navBackStackEntry.safeClick {
                                handleBackNavigation(isNewProduct)
                            }
                        },
                        onProductDone = { isNewProduct: Boolean ->
                            navBackStackEntry.safeClick {
                                handleBackNavigation(isNewProduct)
                            }
                        }
                    )
                }
                composable(
                    route = RootNavDestinations.Details.route,
                    arguments = listOf(
                        navArgument(RootNavDestinations.Details.KEY) {
                            type = NavType.LongType
                        }
                    )
                ) { navBackStackEntry ->
                    fun NavBackStackEntry.getIsFromEdit(defaultValue: Boolean = false): Boolean {
                        return this.savedStateHandle
                            .get<Boolean>(RootNavDestinations.Details.IS_FROM_EDIT)
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
                                    RootNavDestinations.DetailsImage.getRouteToNavigate(
                                        productId = productId,
                                        index = index
                                    )
                                )
                            }
                        },
                        onEditClicked = { id: Long ->
                            navBackStackEntry.safeClick {
                                navController.navigate(
                                    RootNavDestinations.ProductManager.getRouteToNavigate(
                                        id.toString()
                                    )
                                )
                            }
                        },
                        isFromEdit = isFromEdit
                    )
                }
                composable(
                    route = RootNavDestinations.DetailsImage.route,
                    arguments = listOf(
                        navArgument(RootNavDestinations.DetailsImage.KEY_INDEX) {
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
        }
    }
}

@Composable
private fun shouldUseDarkTheme(mainActivityViewState: MainActivityViewState): Boolean =
    when (mainActivityViewState.darkThemeConfig) {
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    }
