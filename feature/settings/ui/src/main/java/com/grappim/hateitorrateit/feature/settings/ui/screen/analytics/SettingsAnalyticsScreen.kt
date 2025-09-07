@file:OptIn(ExperimentalMaterial3Api::class)

package com.grappim.hateitorrateit.feature.settings.ui.screen.analytics

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grappim.hateitorrateit.strings.RString
import com.grappim.hateitorrateit.uikit.icons.PlatoIconType
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewDarkLight
import com.grappim.hateitorrateit.uikit.utils.getColor
import com.grappim.hateitorrateit.uikit.widgets.PlatoHeightSpacer32
import com.grappim.hateitorrateit.uikit.widgets.PlatoHeightSpacer8
import com.grappim.hateitorrateit.uikit.widgets.topbar.LocalTopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarBackButtonState
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarController
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarState
import com.grappim.hateitorrateit.utils.ui.NativeText

const val CRASHLYTICS_TILE_TAG = "crashlytics_tile_tag"
const val ANALYTICS_TILE_TAG = "analytics_tile_tag"

private const val ANIMATION_DURATION = 500

@Composable
fun SettingsAnalyticsScreenRoute(viewModel: SettingsAnalyticsViewModel = hiltViewModel()) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val topBarController: TopBarController = LocalTopBarConfig.current

    LaunchedEffect(Unit) {
        topBarController.update(
            TopBarConfig(
                state = TopBarState.Visible(
                    title = NativeText.Resource(RString.analytics_settings),
                    topBarBackButtonState = TopBarBackButtonState.Visible()
                )
            )
        )
    }

    SettingsAnalyticsScreen(state = state)
}

@Composable
private fun SettingsAnalyticsScreen(state: SettingsAnalyticsViewState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
    ) {
        PlatoHeightSpacer8()

        ListItem(
            modifier = Modifier
                .clickable {
                    state.onCrashlyticsToggle()
                }
                .testTag(CRASHLYTICS_TILE_TAG),
            headlineContent = {
                Text(text = stringResource(id = RString.toggle_crashlytics))
            },
            trailingContent = {
                FeatureEnabledIcon(state.isCrashesCollectionEnabled)
            },
            supportingContent = {
                Text(text = stringResource(id = RString.crashlytics_settings_subtitle))
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.BugReport,
                    contentDescription = "Crashlytics"
                )
            }
        )

        ListItem(
            modifier = Modifier
                .clickable {
                    state.onAnalyticsToggle()
                }
                .testTag(ANALYTICS_TILE_TAG),
            headlineContent = {
                Text(text = stringResource(id = RString.toggle_analytics))
            },
            trailingContent = {
                FeatureEnabledIcon(state.isAnalyticsCollectionEnabled)
            },
            supportingContent = {
                Text(text = stringResource(id = RString.analytics_settings_subtitle))
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.Analytics,
                    contentDescription = "Analytics"
                )
            }
        )

        PlatoHeightSpacer32()
    }
}

@Composable
private fun FeatureEnabledIcon(state: Boolean, modifier: Modifier = Modifier) {
    Crossfade(
        modifier = modifier,
        targetState = state,
        label = "custom_switch_label",
        animationSpec = tween(ANIMATION_DURATION)
    ) { enabled ->
        val imageVector = if (enabled) {
            PlatoIconType.CheckCircleOutline.imageVector
        } else {
            PlatoIconType.HighlightOff.imageVector
        }
        Icon(
            modifier = Modifier
                .testTag(imageVector.name),
            imageVector = imageVector,
            contentDescription = null,
            tint = getColor(isEnabled = enabled)
        )
    }
}

@[Composable PreviewDarkLight]
private fun SettingsAnalyticsScreenGplayPreview() {
    HateItOrRateItTheme {
        SettingsAnalyticsScreen(
            state = SettingsAnalyticsViewState()
        )
    }
}
