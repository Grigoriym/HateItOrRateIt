package com.grappim.hateitorrateit.ui.screens.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.grappim.hateitorrateit.core.navigation.BottomNavDestination
import com.grappim.hateitorrateit.feature.productmanager.ui.navigation.navigateToProductManager
import com.grappim.hateitorrateit.ui.widgets.BottomBar
import com.grappim.hateitorrateit.ui.widgets.PlatoTopAppBar
import com.grappim.hateitorrateit.uikit.icons.PlatoIconType
import com.grappim.hateitorrateit.uikit.widgets.PlatoIcon
import com.grappim.hateitorrateit.uikit.widgets.topbar.LocalTopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarController
import com.grappim.hateitorrateit.utils.ui.asString
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val topBarController = remember { TopBarController() }
    CompositionLocalProvider(
        LocalTopBarConfig provides topBarController
    ) {
        val topBarConfig = topBarController.config
        MainScreenContent(topBarConfig = topBarConfig)
    }
}

@Composable
private fun MainScreenContent(topBarConfig: TopBarConfig) {
    val appState = rememberMainAppState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentColor = MaterialTheme.colorScheme.background,
        topBar = {
            PlatoTopAppBar(
                topBarConfig = topBarConfig,
                goBack = {
                    appState.navController.popBackStack()
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(snackbarData = it)
            }
        },
        floatingActionButton = {
            if (appState.isBottomBarVisible) {
                FloatingActionButton(
                    onClick = {
                        appState.navController.navigateToProductManager()
                    },
                    shape = CircleShape
                ) {
                    PlatoIcon(imageVector = PlatoIconType.Add.imageVector)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            if (appState.isBottomBarVisible) {
                BottomBar(
                    screens = appState.topLevelDestinations,
                    currentBottomNavScreen = appState.currentTopLevelDestination,
                    onBottomNavItemClick = { item: BottomNavDestination ->
                        appState.navigateToTopLevelDestination(item)
                    }
                )
            }
        }
    ) { paddingValues ->
        MainNavHost(
            modifier = Modifier.padding(paddingValues),
            navController = appState.navController,
            showActionSnackbar = { text, action ->
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = text.asString(context),
                        actionLabel = action,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                }
            }
        )
    }
}
