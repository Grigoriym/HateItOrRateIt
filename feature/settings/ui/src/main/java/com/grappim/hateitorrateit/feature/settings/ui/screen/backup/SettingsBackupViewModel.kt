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
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import com.grappim.hateitorrateit.utils.ui.NativeText
import com.grappim.hateitorrateit.utils.ui.SnackbarDelegate
import com.grappim.hateitorrateit.utils.ui.SnackbarDelegateImpl
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
    private val dateTimeUtils: DateTimeUtils
) : ViewModel(),
    SnackbarDelegate by SnackbarDelegateImpl() {

    private val _viewState = MutableStateFlow(
        SettingsBackupViewState(
            onCreateBackup = ::selectBackupLocation,
            onSelectBackupFile = ::selectBackupFile,
            onFilePickerDismissed = ::onFilePickerDismissed,
            onFileSelected = ::onFileSelected,
            onImportModeSelected = ::onImportModeSelected,
            onImportModeDialogDismissed = ::onImportModeDialogDismissed,
            onImportResultDialogDismissed = ::onImportResultDialogDismissed,
            onShowImportResultDialog = ::onShowImportResultDialog,
            onBackupFileSelected = ::createBackup,
            onSaveBackupPickerDismissed = ::onSaveBackupPickerDismissed
        )
    )

    val viewState = _viewState.asStateFlow()

    fun createBackup(backupFileUri: Uri) {
        viewModelScope.launch {
            _viewState.update {
                it.copy(
                    isBackupInProgress = true,
                    lastBackupResult = null,
                    shouldShowSaveBackupPicker = false
                )
            }

            backupRepository.createBackupWithProgress(backupFileUri).collect { state ->
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
                                NativeText.Resource(RString.backup_success)
                            }

                            is BackupResult.PartialSuccess -> {
                                NativeText.Arguments(
                                    RString.backup_partial_success,
                                    listOf(backupResult.warnings.size)
                                )
                            }

                            is BackupResult.Failure -> {
                                NativeText.Arguments(
                                    RString.backup_failed,
                                    listOf(backupResult.message)
                                )
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

    private fun selectBackupLocation() {
        val timestamp = dateTimeUtils.getBackupFolderNowTimestamp()
        val filename = "hateitorrateit_backup_$timestamp.zip"
        _viewState.update {
            it.copy(
                shouldShowSaveBackupPicker = true,
                backupFilename = filename
            )
        }
    }

    private fun onSaveBackupPickerDismissed() {
        viewModelScope.launch {
            _viewState.update { it.copy(shouldShowSaveBackupPicker = false) }

            showSnackbar(NativeText.Resource(RString.backup_file_selection_cancelled))
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
