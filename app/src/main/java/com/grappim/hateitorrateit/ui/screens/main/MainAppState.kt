package com.grappim.hateitorrateit.ui.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.grappim.hateitorrateit.core.navigation.BottomNavDestination
import com.grappim.hateitorrateit.core.navigation.navigate

@Composable
fun rememberMainAppState(navController: NavHostController = rememberNavController()): MainAppState =
    remember(navController) {
        MainAppState(navController)
    }

@Stable
class MainAppState(val navController: NavHostController) {

    private val previousDestination = mutableStateOf<NavDestination?>(null)

    private val currentDestination: NavDestination?
        @Composable get() {
            val currentEntry = navController.currentBackStackEntryFlow
                .collectAsState(initial = null)

            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    val topLevelDestinations = BottomNavDestination.entries

    val currentTopLevelDestination: BottomNavDestination?
        @Composable get() = topLevelDestinations.firstOrNull { dest ->
            currentDestination?.hasRoute(route = dest.route::class) == true
        }

    val isBottomBarVisible: Boolean
        @Composable get() = currentTopLevelDestination != null

    fun navigateToTopLevelDestination(destination: BottomNavDestination) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        destination.navigate(navController, navOptions)
    }
}
