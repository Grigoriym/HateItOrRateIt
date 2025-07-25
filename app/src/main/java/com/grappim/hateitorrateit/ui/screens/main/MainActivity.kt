package com.grappim.hateitorrateit.ui.screens.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.material.snackbar.Snackbar
import com.grappim.hateitorrateit.appupdateapi.AppUpdateChecker
import com.grappim.hateitorrateit.appupdateapi.UpdateState
import com.grappim.hateitorrateit.core.navigation.NavDestinations
import com.grappim.hateitorrateit.data.localdatastorageapi.models.DarkThemeConfig
import com.grappim.hateitorrateit.feature.details.ui.navigation.detailsScreen
import com.grappim.hateitorrateit.feature.productmanager.ui.navigation.productManagerScreen
import com.grappim.hateitorrateit.uikit.R
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.utils.ui.navigation.safeClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var appUpdateChecker: AppUpdateChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        checkForAppUpdates()
        observeUpdateState()

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

    override fun onResume() {
        super.onResume()
        appUpdateChecker.registerUpdateListener()
        appUpdateChecker.checkUpdateStateOnResume()
    }

    override fun onPause() {
        super.onPause()
        appUpdateChecker.unregisterUpdateListener()
    }

    private fun observeUpdateState() {
        lifecycleScope.launch {
            appUpdateChecker.updateState.collectLatest { state ->
                when (state) {
                    is UpdateState.UpdateDownloaded -> showRestartSnackbar()
                }
            }
        }
    }

    private fun showRestartSnackbar() {
        Snackbar.make(
            window.decorView.rootView,
            getString(R.string.app_update_downloaded),
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(getString(R.string.restart)) { appUpdateChecker.completeUpdate() }
            show()
        }
    }

    private fun checkForAppUpdates() {
        if (viewModel.inAppUpdateEnabled.value.not()) {
            return
        }
        appUpdateChecker.checkAndRequestUpdate()
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
