package com.grappim.hateitorrateit.ui.screens.settings

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grappim.hateitorrateit.ui.R
import com.grappim.hateitorrateit.ui.color
import com.grappim.hateitorrateit.ui.icon
import com.grappim.hateitorrateit.ui.theme.AtomicTangerine
import com.grappim.hateitorrateit.ui.theme.Feijoa
import com.grappim.hateitorrateit.ui.utils.PlatoIconType
import com.grappim.hateitorrateit.ui.widgets.PlatoAlertDialog
import com.grappim.hateitorrateit.ui.widgets.PlatoLoadingDialog
import com.grappim.hateitorrateit.ui.widgets.PlatoTopBar

const val CROSSFADE_TAG = "crossfade_tag"
const val CRASHLYTICS_TILE_TAG = "crashlytics_tile_tag"

@Composable
internal fun SettingsRoute(
    goBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        goBack = goBack,
    )
}

@Composable
internal fun SettingsScreen(
    state: SettingsViewState,
    goBack: () -> Unit,
) {
    SettingsScreenContent(
        state = state,
        goBack = goBack,
    )
}

@Composable
private fun SettingsScreenContent(
    state: SettingsViewState,
    goBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            PlatoTopBar(
                text = stringResource(id = R.string.settings),
                goBack = goBack,
            )
        },
    ) { padding ->
        PlatoLoadingDialog(state.isLoading)

        PlatoAlertDialog(
            text = stringResource(id = R.string.are_you_sure_clear_all_data),
            dismissButtonText = stringResource(id = R.string.no),
            showAlertDialog = state.showAlertDialog,
            onDismissRequest = {
                state.onDismissDialog()
            }, onConfirmButtonClicked = {
                state.onAlertDialogConfirmButtonClicked()
            }, onDismissButtonClicked = {
                state.onDismissDialog()
            })

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            ListItem(modifier = Modifier.clickable {
                state.onClearDataClicked()
            }, text = {
                Text(text = stringResource(id = R.string.clear_data))
            })
            ListItem(modifier = Modifier.clickable {
                state.setType()
            }, text = {
                Text(text = stringResource(id = R.string.default_type))
            }, trailing = {
                TypeIcon(state = state)
            })
            ListItem(modifier = Modifier.clickable {
                state.onCrashlyticsToggle()
            }.testTag(CRASHLYTICS_TILE_TAG), text = {
                Text(text = stringResource(id = R.string.toggle_crashlytics))
            }, trailing = {
                CrashesIcon(state)
            }, secondaryText = {
                Text(text = stringResource(id = R.string.crashlytics_settings_subtitle))
            })
        }
    }
}

@Composable
fun TypeIcon(
    state: SettingsViewState
) {
    Crossfade(
        targetState = state.type,
        label = "type_crossfade_icon",
        animationSpec = tween(500),
    ) { type ->
        Icon(
            modifier = Modifier
                .testTag(type.icon().name),
            imageVector = type.icon(),
            contentDescription = null,
            tint = type.color(),
        )
    }
}

@Composable
fun CrashesIcon(
    state: SettingsViewState
) {
    Crossfade(
        modifier = Modifier.testTag(CROSSFADE_TAG),
        targetState = state.isCrashesCollectionEnabled,
        label = "custom_switch_label",
        animationSpec = tween(500),
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
            tint = if (enabled) {
                Feijoa
            } else {
                AtomicTangerine
            },
        )
    }
}
