package com.grappim.hateitorrateit.feature.settings.ui.screen.backup

import android.net.Uri

data class SettingsBackupViewState(
    val isBackupInProgress: Boolean = false,
    val currentOperation: String = "",
    val lastBackupResult: String? = null,
    val isImportInProgress: Boolean = false,
    val currentImportOperation: String = "",
    val lastImportResult: String? = null,
    val onCreateBackup: () -> Unit = {},
    val onOpenDownloadsFolder: () -> Unit = {},
    val onSelectBackupFile: () -> Unit = {},
    val shouldShowFilePicker: Boolean = false,
    val onFilePickerDismissed: () -> Unit = {},
    val onFileSelected: (Uri) -> Unit = {}
)
