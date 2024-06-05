package com.grappim.hateitorrateit.ui.screens.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.grappim.hateitorrateit.core.navigation.NavDestinations
import com.grappim.hateitorrateit.data.localdatastorageapi.models.DarkThemeConfig
import com.grappim.hateitorrateit.feature.details.ui.navigation.detailsScreen
import com.grappim.hateitorrateit.feature.productmanager.ui.navigation.productManagerScreen
import com.grappim.hateitorrateit.uikit.R
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.utils.ui.navigation.safeClick
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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
        if (viewModel.inAppUpdateEnabled.value.not()) {
            return
        }
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
            getString(R.string.app_update_downloaded),
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(getString(R.string.restart)) { appUpdateManager.completeUpdate() }
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
                startDestination = NavDestinations.BottomBarNavDestination.route
            ) {
                composable(NavDestinations.BottomBarNavDestination.route) { navBackStackEntry ->
                    BottomNavigationScreen(
                        darkTheme = darkTheme,
                        goToHateOrRate = {
                            navBackStackEntry.safeClick {
                                navController.navigate(
                                    NavDestinations.ProductManager.getRouteToNavigate("")
                                )
                            }
                        },
                        goToDetails = { id ->
                            navBackStackEntry.safeClick {
                                navController.navigate(
                                    NavDestinations.Details.getRouteToNavigate(
                                        id
                                    )
                                )
                            }
                        }
                    )
                }

                productManagerScreen(navController)
                detailsScreen(navController)
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
