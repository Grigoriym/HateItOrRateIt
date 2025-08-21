package com.grappim.hateitorrateit.feature.settings.ui.screen.backup

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.data.analyticsapi.SettingsAnalytics
import com.grappim.hateitorrateit.data.backupapi.BackupRepository
import com.grappim.hateitorrateit.data.backupapi.ImportRepository
import com.grappim.hateitorrateit.data.backupapi.models.BackupPhase
import com.grappim.hateitorrateit.data.backupapi.models.BackupResult
import com.grappim.hateitorrateit.data.backupapi.models.BackupState
import com.grappim.hateitorrateit.data.backupapi.models.ImportPhase
import com.grappim.hateitorrateit.data.backupapi.models.ImportResult
import com.grappim.hateitorrateit.data.backupapi.models.ImportState
import com.grappim.hateitorrateit.utils.androidapi.IntentGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
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
            onFileSelected = ::importBackup
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

    fun importBackup(backupFileUri: Uri) {
        _viewState.update { it.copy(shouldShowFilePicker = false) }
        viewModelScope.launch {
            _viewState.update {
                it.copy(
                    isImportInProgress = true,
                    lastImportResult = null
                )
            }

            importRepository.importBackupWithProgress(backupFileUri).collect { state ->
                Timber.d("importBackupWithProgress state: $state")
                when (state) {
                    is ImportState.Progress -> {
                        val currentImportOperation = when (state.progress.phase) {
                            ImportPhase.INITIALIZING -> "Initializing import..."
                            ImportPhase.VALIDATING_BACKUP -> "Validating backup file..."
                            ImportPhase.EXTRACTING_DATA -> "Extracting data..."
                            ImportPhase.IMPORTING_PRODUCTS -> "Importing products..."
                            ImportPhase.IMPORTING_IMAGES -> "Importing images..."
                            ImportPhase.IMPORTING_SETTINGS -> "Importing settings..."
                            ImportPhase.FINALIZING -> "Finalizing..."
                            ImportPhase.COMPLETED -> "Completed"
                        }
                        _viewState.update {
                            it.copy(
                                currentImportOperation = currentImportOperation,
                                importProgressPercent = state.progress.percentComplete
                            )
                        }
                    }

                    is ImportState.Completed -> {
                        val result = when (val importResult = state.result) {
                            is ImportResult.Success -> {
                                "Success: Imported ${importResult.importedProducts} products and ${importResult.importedImages} images"
                            }

                            is ImportResult.PartialSuccess -> {
                                "Partial Success: Imported ${importResult.importedProducts} products, ${importResult.importedImages} images with ${importResult.warnings.size} warnings"
                            }

                            is ImportResult.Failure -> {
                                "Error: ${importResult.message}"
                            }
                        }
                        _viewState.update {
                            it.copy(
                                lastImportResult = result,
                                isImportInProgress = false
                            )
                        }
                    }
                }
            }
        }
    }
}
