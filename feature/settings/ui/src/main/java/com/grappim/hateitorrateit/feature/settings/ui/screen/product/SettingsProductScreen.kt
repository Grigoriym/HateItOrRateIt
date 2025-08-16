@file:OptIn(ExperimentalMaterial3Api::class)

package com.grappim.hateitorrateit.feature.settings.ui.screen.product

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
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
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.strings.RString
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewDarkLight
import com.grappim.hateitorrateit.uikit.utils.color
import com.grappim.hateitorrateit.uikit.utils.icon
import com.grappim.hateitorrateit.uikit.widgets.PlatoHeightSpacer32
import com.grappim.hateitorrateit.uikit.widgets.PlatoHeightSpacer8
import com.grappim.hateitorrateit.uikit.widgets.PlatoIcon
import com.grappim.hateitorrateit.uikit.widgets.topbar.LocalTopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarBackButtonState
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarController
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarState
import com.grappim.hateitorrateit.utils.ui.NativeText

private const val ANIMATION_DURATION = 500

@Composable
fun SettingsProductScreenRoute(viewModel: SettingsProductViewModel = hiltViewModel()) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val topBarController: TopBarController = LocalTopBarConfig.current

    LaunchedEffect(Unit) {
        topBarController.update(
            TopBarConfig(
                state = TopBarState.Visible(
                    title = NativeText.Resource(RString.product_settings),
                    topBarBackButtonState = TopBarBackButtonState.Visible()
                )
            )
        )
    }

    SettingsProductScreen(state = state)
}

@Composable
private fun SettingsProductScreen(state: SettingsProductViewState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
    ) {
        PlatoHeightSpacer8()

        ListItem(
            modifier = Modifier.clickable {
                state.setNewType()
            },
            headlineContent = {
                Text(text = stringResource(id = RString.default_type))
            },
            supportingContent = {
                Text(text = stringResource(id = RString.default_type_description))
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.Category,
                    contentDescription = "Default Type"
                )
            },
            trailingContent = {
                TypeIcon(state = state)
            }
        )

        PlatoHeightSpacer32()
    }
}

@Composable
private fun TypeIcon(state: SettingsProductViewState, modifier: Modifier = Modifier) {
    Crossfade(
        modifier = modifier,
        targetState = state.type,
        label = "type_crossfade_icon",
        animationSpec = tween(ANIMATION_DURATION)
    ) { type ->
        PlatoIcon(
            modifier = Modifier
                .testTag(type.icon().name),
            imageVector = type.icon(),
            tint = type.color()
        )
    }
}

@[Composable PreviewDarkLight]
private fun SettingsProductScreenPreview() {
    HateItOrRateItTheme {
        SettingsProductScreen(
            state = SettingsProductViewState(
                type = HateRateType.HATE
            )
        )
    }
}
