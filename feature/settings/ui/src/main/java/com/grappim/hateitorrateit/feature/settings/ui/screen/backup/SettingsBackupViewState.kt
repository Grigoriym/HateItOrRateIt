package com.grappim.hateitorrateit.feature.settings.ui.screen.backup

import android.net.Uri
import com.grappim.hateitorrateit.data.backupapi.models.ImportMode
import com.grappim.hateitorrateit.data.backupapi.models.ImportResult

data class SettingsBackupViewState(
    val isBackupInProgress: Boolean = false,
    val currentOperation: String = "",
    val lastBackupResult: String? = null,
    val isImportInProgress: Boolean = false,
    val currentImportOperation: String = "",
    val lastImportResult: ImportResult? = null,
    val shouldShowImportResultDialog: Boolean = false,
    val onCreateBackup: () -> Unit = {},
    val onOpenDownloadsFolder: () -> Unit = {},
    val onSelectBackupFile: () -> Unit = {},
    val shouldShowFilePicker: Boolean = false,
    val onFilePickerDismissed: () -> Unit = {},
    val onFileSelected: (Uri) -> Unit = {},
    val shouldShowImportModeDialog: Boolean = false,
    val selectedBackupFileUri: Uri? = null,
    val onImportModeSelected: (ImportMode) -> Unit = {},
    val onImportModeDialogDismissed: () -> Unit = {},
    val onImportResultDialogDismissed: () -> Unit = {},
    val onShowImportResultDialog: () -> Unit = {}
)
