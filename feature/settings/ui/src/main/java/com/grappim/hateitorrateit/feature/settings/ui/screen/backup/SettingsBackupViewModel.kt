package com.grappim.hateitorrateit.feature.settings.ui.screen.backup

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
import com.grappim.hateitorrateit.data.backupapi.models.ImportState
import com.grappim.hateitorrateit.strings.RString
import com.grappim.hateitorrateit.utils.androidapi.IntentGenerator
import com.grappim.hateitorrateit.utils.ui.IntentActionDelegate
import com.grappim.hateitorrateit.utils.ui.IntentActionDelegateImpl
import com.grappim.hateitorrateit.utils.ui.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsBackupViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val importRepository: ImportRepository,
    private val intentGenerator: IntentGenerator
) : ViewModel(),
    IntentActionDelegate by IntentActionDelegateImpl() {

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
                            BackupPhase.INITIALIZING -> NativeText.Resource(
                                RString.backup_initializing
                            )
                            BackupPhase.COLLECTING_DATABASE_DATA -> NativeText.Resource(
                                RString.backup_collecting_data
                            )
                            BackupPhase.COLLECTING_IMAGES -> NativeText.Resource(
                                RString.backup_collecting_images
                            )
                            BackupPhase.CREATING_BACKUP_FILE -> NativeText.Resource(
                                RString.backup_creating_file
                            )
                            BackupPhase.FINALIZING -> NativeText.Resource(RString.backup_finalizing)
                            BackupPhase.COMPLETED -> NativeText.Resource(RString.backup_completed)
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

    private fun openDownloadsFolder() {
        viewModelScope.launch {
            val intent = intentGenerator.generateOpenDownloadsFolderIntent()
            useIntentAction(intent)
        }
    }

    private fun selectBackupFile() {
        _viewState.update { it.copy(shouldShowFilePicker = true) }
    }

    private fun onFilePickerDismissed() {
        _viewState.update { it.copy(shouldShowFilePicker = false) }
    }

    private fun onFileSelected(backupFileUri: Uri) {
        _viewState.update {
            it.copy(
                shouldShowFilePicker = false,
                shouldShowImportModeDialog = true,
                selectedBackupFileUri = backupFileUri
            )
        }
    }

    private fun onImportModeSelected(importMode: ImportMode) {
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

    private fun onImportModeDialogDismissed() {
        _viewState.update {
            it.copy(
                shouldShowImportModeDialog = false,
                selectedBackupFileUri = null
            )
        }
    }

    private fun onImportResultDialogDismissed() {
        _viewState.update {
            it.copy(shouldShowImportResultDialog = false)
        }
    }

    private fun onShowImportResultDialog() {
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
                            ImportPhase.VALIDATING_BACKUP -> NativeText.Resource(
                                RString.import_validating_backup
                            )
                            ImportPhase.EXTRACTING_DATA -> NativeText.Resource(
                                RString.import_extracting_data
                            )
                            ImportPhase.IMPORTING_PRODUCTS -> NativeText.Resource(
                                RString.import_importing_products
                            )
                            ImportPhase.IMPORTING_IMAGES -> NativeText.Resource(
                                RString.import_importing_images
                            )
                            ImportPhase.IMPORTING_SETTINGS -> NativeText.Resource(
                                RString.import_importing_settings
                            )
                            ImportPhase.FINALIZING -> NativeText.Resource(RString.import_finalizing)
                            ImportPhase.COMPLETED -> NativeText.Resource(RString.import_completed)
                            ImportPhase.DETECTING_CONFLICTS -> NativeText.Resource(
                                RString.import_detecting_conflicts
                            )
                            ImportPhase.RESOLVING_CONFLICTS -> NativeText.Resource(
                                RString.import_resolving_conflicts
                            )
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
