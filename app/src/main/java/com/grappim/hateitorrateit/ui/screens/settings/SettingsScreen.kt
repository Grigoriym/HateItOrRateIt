package com.grappim.hateitorrateit.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        onClearDataClicked = {
            viewModel.clearData()
        }
    )
}

@Composable
private fun SettingsScreenContent(
    state: SettingsViewState,
    goBack: () -> Unit,
    onClearDataClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            PlatoTopBar(
                text = "Settings",
                goBack = goBack,
            )
        }
    ) {
        PlatoLoadingDialog(state.isLoading)
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            ListItem(
                modifier = Modifier
                    .clickable {
                        onClearDataClicked()
                    },
                text = {
                    Text(text = "Clear Data")
                })
        }
    }
}
