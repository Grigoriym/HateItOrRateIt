package com.grappim.hateitorrateit.feature.settings.ui.screen.backup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.data.backupapi.BackupRepository
import com.grappim.hateitorrateit.data.backupapi.ImportRepository
import com.grappim.hateitorrateit.data.backupapi.models.BackupPhase
import com.grappim.hateitorrateit.data.backupapi.models.BackupResult
import com.grappim.hateitorrateit.data.backupapi.models.BackupState
import com.grappim.hateitorrateit.data.backupapi.models.ImportMode
import com.grappim.hateitorrateit.data.backupapi.models.ImportPhase
import com.grappim.hateitorrateit.data.backupapi.models.ImportResult
import com.grappim.hateitorrateit.data.backupapi.models.ImportState
import com.grappim.hateitorrateit.utils.androidapi.IntentGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsBackupViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val backupRepository: BackupRepository,
    private val importRepository: ImportRepository,
    private val intentGenerator: IntentGenerator
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SettingsBackupViewState(
            onCreateBackup = ::createBackup,
            onOpenDownloadsFolder = ::openDownloadsFolder,
            onSelectBackupFile = ::selectBackupFile,
            onFilePickerDismissed = ::onFilePickerDismissed,
            onFileSelected = ::onFileSelected,
            onImportModeSelected = ::onImportModeSelected,
            onImportModeDialogDismissed = ::onImportModeDialogDismissed,
            onImportResultDialogDismissed = ::onImportResultDialogDismissed,
            onShowImportResultDialog = ::onShowImportResultDialog
        )
    )

    val viewState = _viewState.asStateFlow()

    fun createBackup() {
        viewModelScope.launch {
            _viewState.update { it.copy(isBackupInProgress = true, lastBackupResult = null) }

            backupRepository.createBackupWithProgress().collect { state ->
                Timber.d("createBackupWithProgress state: $state")
                when (state) {
                    is BackupState.Progress -> {
                        val currentOperation = when (state.progress.phase) {
                            BackupPhase.INITIALIZING -> "Initializing backup..."
                            BackupPhase.COLLECTING_DATABASE_DATA -> "Collecting data..."
                            BackupPhase.COLLECTING_IMAGES -> "Collecting images..."
                            BackupPhase.CREATING_BACKUP_FILE -> "Creating backup file..."
                            BackupPhase.FINALIZING -> "Finalizing..."
                            BackupPhase.COMPLETED -> "Completed"
                        }
                        _viewState.update {
                            it.copy(
                                currentOperation = currentOperation
                            )
                        }
                    }

                    is BackupState.Completed -> {
                        val result = when (val backupResult = state.result) {
                            is BackupResult.Success -> {
                                "Success: Backup saved to ${backupResult.backupFile.name}"
                            }

                            is BackupResult.PartialSuccess -> {
                                "Partial Success: Backup created with ${backupResult.warnings.size} warnings"
                            }

                            is BackupResult.Failure -> {
                                "Error: ${backupResult.message}"
                            }
                        }
                        _viewState.update {
                            it.copy(
                                lastBackupResult = result,
                                isBackupInProgress = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun openDownloadsFolder() {
        try {
            val intent = intentGenerator.generateOpenDownloadsFolderIntent()
            context.startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to open Downloads folder")
        }
    }

    fun selectBackupFile() {
        _viewState.update { it.copy(shouldShowFilePicker = true) }
    }

    fun onFilePickerDismissed() {
        _viewState.update { it.copy(shouldShowFilePicker = false) }
    }

    fun onFileSelected(backupFileUri: Uri) {
        _viewState.update {
            it.copy(
                shouldShowFilePicker = false,
                shouldShowImportModeDialog = true,
                selectedBackupFileUri = backupFileUri
            )
        }
    }

    fun onImportModeSelected(importMode: ImportMode) {
        val backupFileUri = _viewState.value.selectedBackupFileUri
        if (backupFileUri != null) {
            _viewState.update {
                it.copy(
                    shouldShowImportModeDialog = false,
                    selectedBackupFileUri = null
                )
            }
            importBackup(backupFileUri, importMode)
        }
    }

    fun onImportModeDialogDismissed() {
        _viewState.update {
            it.copy(
                shouldShowImportModeDialog = false,
                selectedBackupFileUri = null
            )
        }
    }

    fun onImportResultDialogDismissed() {
        _viewState.update {
            it.copy(shouldShowImportResultDialog = false)
        }
    }

    fun onShowImportResultDialog() {
        _viewState.update {
            it.copy(shouldShowImportResultDialog = true)
        }
    }

    private fun importBackup(backupFileUri: Uri, importMode: ImportMode) {
        viewModelScope.launch {
            _viewState.update {
                it.copy(
                    isImportInProgress = true,
                    lastImportResult = null
                )
            }

            importRepository.importBackupWithProgress(backupFileUri, importMode).collect { state ->
                Timber.d("importBackupWithProgress state: $state")
                when (state) {
                    is ImportState.Progress -> {
                        val currentImportOperation = when (state.progress.phase) {
                            ImportPhase.VALIDATING_BACKUP -> "Validating backup file..."
                            ImportPhase.EXTRACTING_DATA -> "Extracting data..."
                            ImportPhase.IMPORTING_PRODUCTS -> "Importing products..."
                            ImportPhase.IMPORTING_IMAGES -> "Importing images..."
                            ImportPhase.IMPORTING_SETTINGS -> "Importing settings..."
                            ImportPhase.FINALIZING -> "Finalizing..."
                            ImportPhase.COMPLETED -> "Completed"
                            ImportPhase.DETECTING_CONFLICTS -> "Detecting conflicts..."
                            ImportPhase.RESOLVING_CONFLICTS -> "Resolving conflicts..."
                        }
                        _viewState.update {
                            it.copy(
                                currentImportOperation = currentImportOperation
                            )
                        }
                    }

                    is ImportState.Completed -> {
                        _viewState.update {
                            it.copy(
                                lastImportResult = state.result,
                                shouldShowImportResultDialog = true,
                                isImportInProgress = false
                            )
                        }
                    }
                }
            }
        }
    }
}
