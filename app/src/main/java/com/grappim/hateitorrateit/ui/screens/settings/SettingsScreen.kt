package com.grappim.hateitorrateit.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
        ) {
            ListItem(
                modifier = Modifier
                    .clickable {
                        onClearDataClicked()
                    },
                headlineContent = {
                    Text(text = "Clear Data")
                })
        }
    }
}
