@file:OptIn(ExperimentalMaterial3Api::class)

package com.grappim.hateitorrateit.feature.settings.ui.screen.database

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import com.grappim.hateitorrateit.uikit.widgets.PlatoAlertDialog
import com.grappim.hateitorrateit.uikit.widgets.PlatoHeightSpacer32
import com.grappim.hateitorrateit.uikit.widgets.PlatoHeightSpacer8
import com.grappim.hateitorrateit.uikit.widgets.PlatoLoadingDialog
import com.grappim.hateitorrateit.uikit.widgets.topbar.LocalTopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarBackButtonState
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarController
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarState
import com.grappim.hateitorrateit.utils.ui.NativeText

@Composable
fun SettingsDatabaseScreenRoute(viewModel: SettingsDatabaseViewModel = hiltViewModel()) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val topBarController: TopBarController = LocalTopBarConfig.current

    LaunchedEffect(Unit) {
        topBarController.update(
            TopBarConfig(
                state = TopBarState.Visible(
                    title = NativeText.Resource(RString.database_settings),
                    topBarBackButtonState = TopBarBackButtonState.Visible()
                )
            )
        )
    }

    PlatoLoadingDialog(state.isLoading)

    PlatoAlertDialog(
        text = stringResource(id = RString.are_you_sure_clear_all_data),
        dismissButtonText = stringResource(id = RString.no),
        showAlertDialog = state.showAlertDialog,
        onDismissRequest = {
            state.onDismissDialog()
        },
        onConfirmButtonClick = {
            state.onAlertDialogConfirmButtonClicked()
        },
        onDismissButtonClick = {
            state.onDismissDialog()
        }
    )

    SettingsDatabaseScreen(state = state)
}

@Composable
private fun SettingsDatabaseScreen(state: SettingsDatabaseViewState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
    ) {
        PlatoHeightSpacer8()

        ListItem(
            modifier = Modifier.clickable {
                state.onClearDataClicked()
            },
            headlineContent = {
                Text(text = stringResource(id = RString.clear_data))
            },
            supportingContent = {
                Text(text = stringResource(id = RString.clear_data_description))
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.DeleteSweep,
                    contentDescription = "Clear Data"
                )
            }
        )

        PlatoHeightSpacer32()
    }
}

@[Composable PreviewDarkLight]
private fun SettingsDatabaseScreenPreview() {
    HateItOrRateItTheme {
        SettingsDatabaseScreen(
            state = SettingsDatabaseViewState()
        )
    }
}
