@file:OptIn(ExperimentalMaterial3Api::class)

package com.grappim.hateitorrateit.feature.settings.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grappim.hateitorrateit.strings.RString
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewDarkLight
import com.grappim.hateitorrateit.uikit.widgets.PlatoHeightSpacer32
import com.grappim.hateitorrateit.uikit.widgets.PlatoHeightSpacer8
import com.grappim.hateitorrateit.uikit.widgets.topbar.LocalTopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarBackButtonState
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarController
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarState
import com.grappim.hateitorrateit.utils.ui.NativeText

@Composable
fun SettingsRoute(
    goToAboutScreen: () -> Unit,
    goToInterfaceScreen: () -> Unit,
    goToDatabaseScreen: () -> Unit,
    goToAnalyticsScreen: () -> Unit,
    goToProductScreen: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val topBarController: TopBarController = LocalTopBarConfig.current

    LaunchedEffect(Unit) {
        topBarController.update(
            TopBarConfig(
                state = TopBarState.Visible(
                    title = NativeText.Resource(RString.settings),
                    topBarBackButtonState = TopBarBackButtonState.Hidden
                )
            )
        )
    }

    DisposableEffect(Unit) {
        state.trackScreenStart()
        onDispose {}
    }

    SettingsScreen(
        state = state,
        goToAboutScreen = goToAboutScreen,
        goToInterfaceScreen = goToInterfaceScreen,
        goToDatabaseScreen = goToDatabaseScreen,
        goToAnalyticsScreen = goToAnalyticsScreen,
        goToProductScreen = goToProductScreen
    )
}

@Composable
private fun SettingsScreen(
    state: SettingsViewState,
    goToAboutScreen: () -> Unit = {},
    goToInterfaceScreen: () -> Unit = {},
    goToDatabaseScreen: () -> Unit = {},
    goToAnalyticsScreen: () -> Unit = {},
    goToProductScreen: () -> Unit = {}
) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
        ) {
            PlatoHeightSpacer8()

            ListItem(
                modifier = Modifier
                    .clickable {
                        goToProductScreen()
                    },
                headlineContent = {
                    Text(text = stringResource(RString.product_settings))
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Category,
                        contentDescription = "Product Settings"
                    )
                }
            )

            if (!state.isFdroidBuild) {
                ListItem(
                    modifier = Modifier
                        .clickable {
                            goToAnalyticsScreen()
                        },
                    headlineContent = {
                        Text(text = stringResource(RString.analytics_settings))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Analytics,
                            contentDescription = "Analytics Settings"
                        )
                    }
                )
            }

            ListItem(
                modifier = Modifier
                    .clickable {
                        goToDatabaseScreen()
                    },
                headlineContent = {
                    Text(text = stringResource(RString.database_settings))
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Storage,
                        contentDescription = "Database Settings"
                    )
                }
            )

            ListItem(
                modifier = Modifier
                    .clickable {
                        goToInterfaceScreen()
                    },
                headlineContent = {
                    Text(text = stringResource(RString.interface_screen_name))
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.TouchApp,
                        contentDescription = "Interface Screen"
                    )
                }
            )

            ListItem(
                modifier = Modifier
                    .clickable {
                        goToAboutScreen()
                    },
                headlineContent = {
                    Text(text = stringResource(RString.about))
                },
                leadingContent = {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = "About Screen")
                }
            )

            PlatoHeightSpacer32()
        }
    }
}

@[Composable PreviewDarkLight]
private fun SettingsScreenFdroidPreview() {
    HateItOrRateItTheme {
        SettingsScreen(
            state = SettingsViewState(
                isFdroidBuild = true
            )
        )
    }
}

@[Composable PreviewDarkLight]
private fun SettingsScreenGplayPreview() {
    HateItOrRateItTheme {
        SettingsScreen(
            state = SettingsViewState(
                isFdroidBuild = false
            )
        )
    }
}
