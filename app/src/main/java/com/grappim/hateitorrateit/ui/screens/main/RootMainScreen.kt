package com.grappim.hateitorrateit.ui.screens.main

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
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.grappim.hateitorrateit.feature.home.ui.navigation.HomeNavScreen
import com.grappim.hateitorrateit.feature.home.ui.navigation.homeScreen
import com.grappim.hateitorrateit.feature.settings.ui.navigation.SettingsNavScreen
import com.grappim.hateitorrateit.feature.settings.ui.navigation.settingsScreen
import com.grappim.hateitorrateit.uikit.theme.bottomNavigationBackgroundDark
import com.grappim.hateitorrateit.uikit.theme.bottomNavigationBackgroundLight
import com.grappim.hateitorrateit.uikit.widgets.PlatoIcon
import com.grappim.hateitorrateit.utils.ui.PlatoIconType

@Composable
fun RootMainScreen(
    goToHateOrRate: () -> Unit,
    goToDetails: (id: Long) -> Unit,
    darkTheme: Boolean
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
                shape = CircleShape
            ) {
                PlatoIcon(imageVector = PlatoIconType.Add.imageVector)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        bottomBar = {
            val screens = listOf(
                HomeNavScreen,
                SettingsNavScreen
            )
            BottomNavigation(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp
                        )
                    ),
                backgroundColor = if (darkTheme) {
                    bottomNavigationBackgroundDark
                } else {
                    bottomNavigationBackgroundLight
                }
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    BottomNavigationItem(
                        selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == screen.route } == true,
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
                        icon = { PlatoIcon(imageVector = screen.imageVector) }
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
                startDestination = HomeNavScreen.route
            ) {
                homeScreen(goToDetails)
                settingsScreen(navController)
            }
        }
    }
}
