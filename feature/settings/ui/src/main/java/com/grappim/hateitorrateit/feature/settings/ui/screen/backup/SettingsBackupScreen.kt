@file:OptIn(ExperimentalMaterial3Api::class)

package com.grappim.hateitorrateit.feature.settings.ui.screen.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grappim.hateitorrateit.strings.RString
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewDarkLight
import com.grappim.hateitorrateit.uikit.widgets.PlatoHeightSpacer16
import com.grappim.hateitorrateit.uikit.widgets.topbar.LocalTopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarBackButtonState
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarController
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarState
import com.grappim.hateitorrateit.utils.ui.NativeText

@Composable
fun SettingsBackupRoute(viewModel: SettingsBackupViewModel = hiltViewModel()) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val topBarController: TopBarController = LocalTopBarConfig.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { state.onFileSelected(it) }
            ?: state.onFilePickerDismissed()
    }

    LaunchedEffect(state.shouldShowFilePicker) {
        if (state.shouldShowFilePicker) {
            filePickerLauncher.launch("application/zip")
        }
    }

    LaunchedEffect(Unit) {
        topBarController.update(
            TopBarConfig(
                state = TopBarState.Visible(
                    title = NativeText.Resource(RString.backup_settings),
                    topBarBackButtonState = TopBarBackButtonState.Visible()
                )
            )
        )
    }

    SettingsBackupScreen(state = state)
}

@Composable
private fun SettingsBackupScreen(state: SettingsBackupViewState) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ExportDataWidget(state = state)

            PlatoHeightSpacer16()

            ImportDataWidget(state = state)
        }
    }
}

@Composable
private fun ExportDataWidget(state: SettingsBackupViewState) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(RString.export_data),
                style = MaterialTheme.typography.headlineSmall
            )

            PlatoHeightSpacer16()

            Text(
                text = stringResource(RString.export_data_description),
                style = MaterialTheme.typography.bodyMedium
            )

            PlatoHeightSpacer16()

            if (state.isBackupInProgress) {
                Column {
                    Text(
                        text = state.currentOperation,
                        style = MaterialTheme.typography.bodySmall
                    )
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    OutlinedButton(
                        onClick = state.onOpenDownloadsFolder,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Folder,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(RString.open_downloads))
                    }

                    OutlinedButton(
                        onClick = state.onCreateBackup,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(RString.create_backup))
                    }
                }
            }

            state.lastBackupResult?.let { result ->
                PlatoHeightSpacer16()
                Text(
                    text = result,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (result.contains("Success")) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}

@Composable
private fun ImportDataWidget(state: SettingsBackupViewState) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(RString.import_data),
                style = MaterialTheme.typography.headlineSmall
            )

            PlatoHeightSpacer16()

            Text(
                text = stringResource(RString.import_data_description),
                style = MaterialTheme.typography.bodyMedium
            )

            PlatoHeightSpacer16()

            if (state.isImportInProgress) {
                Column {
                    Text(
                        text = state.currentImportOperation,
                        style = MaterialTheme.typography.bodySmall
                    )
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            } else {
                OutlinedButton(
                    onClick = state.onSelectBackupFile,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Upload,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(RString.select_backup_file))
                }
            }

            state.lastImportResult?.let { result ->
                PlatoHeightSpacer16()
                Text(
                    text = result,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (result.contains("Success")) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}

@PreviewDarkLight
@Composable
private fun SettingsBackupScreenPreview() {
    HateItOrRateItTheme {
        SettingsBackupScreen(
            state = SettingsBackupViewState()
        )
    }
}
