package com.grappim.hateitorrateit.feature.settings.ui.screen.backup

import android.net.Uri
import com.grappim.hateitorrateit.data.backupapi.models.ImportMode
import com.grappim.hateitorrateit.data.backupapi.models.ImportResult
import com.grappim.hateitorrateit.utils.ui.NativeText

data class SettingsBackupViewState(
    val isBackupInProgress: Boolean = false,
    val currentOperation: NativeText = NativeText.Empty,
    val lastBackupResult: String? = null,
    val isImportInProgress: Boolean = false,
    val currentImportOperation: NativeText = NativeText.Empty,
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
