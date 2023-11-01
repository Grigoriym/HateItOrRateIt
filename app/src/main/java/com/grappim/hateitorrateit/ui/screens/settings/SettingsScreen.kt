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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grappim.ui.R
import com.grappim.ui.color
import com.grappim.ui.icon
import com.grappim.ui.widgets.PlatoAlertDialog
import com.grappim.ui.widgets.PlatoLoadingDialog
import com.grappim.ui.widgets.PlatoTopBar

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    goBack: () -> Unit,
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
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
    Scaffold(topBar = {
        PlatoTopBar(
            text = stringResource(id = R.string.settings),
            goBack = goBack,
        )
    }) {
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
                .padding(it)
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
        }
    }
}

@Composable
fun TypeIcon(
    state: SettingsViewState
) {
    Crossfade(
        targetState = state.type,
        label = "custom_switch_label",
        animationSpec = tween(500),
    ) {
        Icon(
            modifier = Modifier,
            imageVector = it.icon(),
            contentDescription = null,
            tint = it.color(),
        )
    }
}
