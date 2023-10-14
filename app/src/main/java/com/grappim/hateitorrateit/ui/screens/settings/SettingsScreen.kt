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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grappim.hateitorrateit.utils.color
import com.grappim.hateitorrateit.utils.icon
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
            text = "Settings",
            goBack = goBack,
        )
    }) {
        PlatoLoadingDialog(state.isLoading)

        PlatoAlertDialog(
            text = "Are you sure you want to clear all the data?",
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
                Text(text = "Clear Data")
            })
            ListItem(modifier = Modifier.clickable {
                state.setType()
            }, text = {
                Text(text = "Default Type")
            }, trailing = {
                CustomSwitch(state = state)
            })
        }
    }
}

@Composable
fun CustomSwitch(
    state: SettingsViewState
) {
    Crossfade(
        targetState = state.type,
        label = "",
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
