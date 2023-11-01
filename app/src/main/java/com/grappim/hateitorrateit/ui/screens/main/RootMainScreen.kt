package com.grappim.hateitorrateit.ui.screens.main

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.grappim.hateitorrateit.core.navigation.HomeNavDestination
import com.grappim.hateitorrateit.ui.screens.home.HomeScreen
import com.grappim.hateitorrateit.ui.screens.settings.SettingsScreen
import com.grappim.hateitorrateit.utils.safeClick
import com.grappim.ui.theme.bottomNavigationBackgroundDark
import com.grappim.ui.theme.bottomNavigationBackgroundLight

@Composable
fun RootMainScreen(
    rootViewModel: MainActivityViewModel,
    goToHateOrRate: () -> Unit,
    goToDetails: (id: Long) -> Unit,
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    goToHateOrRate()
                },
                shape = CircleShape,
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        bottomBar = {
            val screens = listOf(
                HomeNavDestination.Home,
                HomeNavDestination.Settings
            )
            BottomNavigation(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp
                        )
                    ),
                backgroundColor = if (isSystemInDarkTheme()) bottomNavigationBackgroundDark
                else bottomNavigationBackgroundLight
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    BottomNavigationItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        label = {
                            Text(text = screen.title)
                        },
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.imageVector, contentDescription = null) },
                    )
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            NavHost(
                navController = navController,
                startDestination = HomeNavDestination.Home.route,
            ) {
                composable(HomeNavDestination.Home.route) { navBackStackEntry ->
                    HomeScreen(
                        onDocumentClick = { productId: Long ->
                            navBackStackEntry.safeClick {
                                goToDetails(productId)
                            }
                        }
                    )
                }
                composable(HomeNavDestination.Settings.route) { navBackStackEntry ->
                    SettingsScreen(
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
