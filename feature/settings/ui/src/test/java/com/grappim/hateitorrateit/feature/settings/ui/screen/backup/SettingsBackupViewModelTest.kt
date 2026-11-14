package com.grappim.hateitorrateit.feature.settings.ui.screen.backup

import app.cash.turbine.test
import com.grappim.hateitorrateit.data.backupapi.BackupRepository
import com.grappim.hateitorrateit.data.backupapi.ImportRepository
import com.grappim.hateitorrateit.data.backupapi.models.BackupError
import com.grappim.hateitorrateit.data.backupapi.models.BackupPhase
import com.grappim.hateitorrateit.data.backupapi.models.BackupProgress
import com.grappim.hateitorrateit.data.backupapi.models.BackupResult
import com.grappim.hateitorrateit.data.backupapi.models.BackupState
import com.grappim.hateitorrateit.data.backupapi.models.ImportMode
import com.grappim.hateitorrateit.data.backupapi.models.ImportPhase
import com.grappim.hateitorrateit.data.backupapi.models.ImportProgress
import com.grappim.hateitorrateit.data.backupapi.models.ImportResult
import com.grappim.hateitorrateit.data.backupapi.models.ImportState
import com.grappim.hateitorrateit.strings.RString
import com.grappim.hateitorrateit.testing.core.MainDispatcherRule
import com.grappim.hateitorrateit.testing.core.fakeUri
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import com.grappim.hateitorrateit.utils.ui.NativeText
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SettingsBackupViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val backupRepository: BackupRepository = mockk()
    private val importRepository: ImportRepository = mockk()

    private val dateTimeUtils: DateTimeUtils = mockk()
    lateinit var sut: SettingsBackupViewModel

    @Before
    fun setup() {
        sut = SettingsBackupViewModel(
            backupRepository = backupRepository,
            importRepository = importRepository,
            dateTimeUtils = dateTimeUtils
        )
    }

    @Test
    fun `on selectBackupFile should set to true`() {
        assertFalse(sut.viewState.value.shouldShowFilePicker)

        sut.viewState.value.onSelectBackupFile()

        assertTrue(sut.viewState.value.shouldShowFilePicker)
    }

    @Test
    fun `on onFilePickerDismissed should set to false`() {
        sut.viewState.value.onSelectBackupFile()
        assertTrue(sut.viewState.value.shouldShowFilePicker)

        sut.viewState.value.onFilePickerDismissed()

        assertFalse(sut.viewState.value.shouldShowFilePicker)
    }

    @Test
    fun `on onShowImportResultDialog should set to true`() {
        assertFalse(sut.viewState.value.shouldShowImportResultDialog)

        sut.viewState.value.onShowImportResultDialog()

        assertTrue(sut.viewState.value.shouldShowImportResultDialog)
    }

    @Test
    fun `on onImportResultDialogDismissed should set to false`() {
        sut.viewState.value.onShowImportResultDialog()
        assertTrue(sut.viewState.value.shouldShowImportResultDialog)

        sut.viewState.value.onImportResultDialogDismissed()

        assertFalse(sut.viewState.value.shouldShowImportResultDialog)
    }

    @Test
    fun `on onImportModeDialogDismissed should set to true`() {
        sut.viewState.value.onImportModeDialogDismissed()

        assertFalse(sut.viewState.value.shouldShowImportModeDialog)
        assertNull(sut.viewState.value.selectedBackupFileUri)
    }

    @Test
    fun `on onFileSelected should store uri and show import mode dialog`() {
        val uri = fakeUri()

        sut.viewState.value.onFileSelected(uri)

        assertEquals(uri, sut.viewState.value.selectedBackupFileUri)
        assertTrue(sut.viewState.value.shouldShowImportModeDialog)
        assertFalse(sut.viewState.value.shouldShowFilePicker)
    }

    @Test
    fun `on onImportModeSelected without selected file should do nothing`() {
        sut.viewState.value.onImportModeSelected(ImportMode.CREATE_NEW)

        assertFalse(sut.viewState.value.isImportInProgress)
        coVerify(exactly = 0) { importRepository.importBackupWithProgress(any(), any()) }
    }

    @Test
    fun `on onCreateBackup should show save picker with timestamped filename`() {
        every { dateTimeUtils.getBackupFolderNowTimestamp() } returns "2026_01_02"

        sut.viewState.value.onCreateBackup()

        assertTrue(sut.viewState.value.shouldShowSaveBackupPicker)
        assertEquals("hateitorrateit_backup_2026_01_02.zip", sut.viewState.value.backupFilename)
    }

    @Test
    fun `on onSaveBackupPickerDismissed should hide picker and show snackbar`() = runTest {
        sut.snackBarMessage.test {
            sut.viewState.value.onSaveBackupPickerDismissed()

            assertEquals(
                NativeText.Resource(RString.backup_file_selection_cancelled),
                awaitItem()
            )
        }
        assertFalse(sut.viewState.value.shouldShowSaveBackupPicker)
    }

    @Test
    fun `backup in progress should expose current operation`() {
        val uri = fakeUri()
        val gate = CompletableDeferred<Unit>()
        coEvery { backupRepository.createBackupWithProgress(uri) } returns flow {
            emit(BackupState.Progress(BackupProgress(BackupPhase.CREATING_BACKUP_FILE)))
            gate.await()
        }

        sut.viewState.value.onBackupFileSelected(uri)

        assertTrue(sut.viewState.value.isBackupInProgress)
        assertEquals(
            NativeText.Resource(RString.backup_creating_file),
            sut.viewState.value.currentOperation
        )
        gate.complete(Unit)
    }

    @Test
    fun `every backup phase maps to an operation text`() {
        val expected = mapOf(
            BackupPhase.INITIALIZING to RString.backup_initializing,
            BackupPhase.COLLECTING_DATABASE_DATA to RString.backup_collecting_data,
            BackupPhase.COLLECTING_IMAGES to RString.backup_collecting_images,
            BackupPhase.CREATING_BACKUP_FILE to RString.backup_creating_file,
            BackupPhase.FINALIZING to RString.backup_finalizing,
            BackupPhase.COMPLETED to RString.backup_completed
        )
        expected.forEach { (phase, text) ->
            val uri = fakeUri()
            coEvery { backupRepository.createBackupWithProgress(uri) } returns flowOf(
                BackupState.Progress(BackupProgress(phase))
            )

            sut.createBackup(uri)

            assertEquals(NativeText.Resource(text), sut.viewState.value.currentOperation)
        }
    }

    @Test
    fun `successful backup should finish with success text`() {
        assertBackupResult(
            BackupResult.Success,
            NativeText.Resource(RString.backup_success)
        )
    }

    @Test
    fun `partial backup should finish with warnings count`() {
        assertBackupResult(
            BackupResult.PartialSuccess(missingImages = emptyList(), warnings = listOf("a", "b")),
            NativeText.Arguments(RString.backup_partial_success, listOf(2))
        )
    }

    @Test
    fun `failed backup should finish with failure message`() {
        assertBackupResult(
            BackupResult.Failure(BackupError.UNKNOWN_ERROR, "disk full"),
            NativeText.Arguments(RString.backup_failed, listOf("disk full"))
        )
    }

    @Test
    fun `import flow should run with selected mode and show result dialog`() {
        val uri = fakeUri()
        val result = ImportResult.Success(
            importedProducts = 3,
            importedImages = 4,
            importedSettings = true
        )
        coEvery {
            importRepository.importBackupWithProgress(uri, ImportMode.SKIP_CONFLICTS)
        } returns flowOf(
            ImportState.Progress(ImportProgress(ImportPhase.IMPORTING_PRODUCTS)),
            ImportState.Completed(result)
        )

        sut.viewState.value.onFileSelected(uri)
        sut.viewState.value.onImportModeSelected(ImportMode.SKIP_CONFLICTS)

        val state = sut.viewState.value
        assertFalse(state.shouldShowImportModeDialog)
        assertNull(state.selectedBackupFileUri)
        assertFalse(state.isImportInProgress)
        assertTrue(state.shouldShowImportResultDialog)
        assertEquals(result, state.lastImportResult)
        assertEquals(
            NativeText.Resource(RString.import_importing_products),
            state.currentImportOperation
        )
    }

    @Test
    fun `every import phase maps to an operation text`() {
        val expected = mapOf(
            ImportPhase.VALIDATING_BACKUP to RString.import_validating_backup,
            ImportPhase.EXTRACTING_DATA to RString.import_extracting_data,
            ImportPhase.DETECTING_CONFLICTS to RString.import_detecting_conflicts,
            ImportPhase.RESOLVING_CONFLICTS to RString.import_resolving_conflicts,
            ImportPhase.IMPORTING_PRODUCTS to RString.import_importing_products,
            ImportPhase.IMPORTING_IMAGES to RString.import_importing_images,
            ImportPhase.IMPORTING_SETTINGS to RString.import_importing_settings,
            ImportPhase.FINALIZING to RString.import_finalizing,
            ImportPhase.COMPLETED to RString.import_completed
        )
        expected.forEach { (phase, text) ->
            val uri = fakeUri()
            coEvery {
                importRepository.importBackupWithProgress(uri, ImportMode.CREATE_NEW)
            } returns
                flowOf(ImportState.Progress(ImportProgress(phase)))

            sut.viewState.value.onFileSelected(uri)
            sut.viewState.value.onImportModeSelected(ImportMode.CREATE_NEW)

            assertEquals(NativeText.Resource(text), sut.viewState.value.currentImportOperation)
        }
    }

    private fun assertBackupResult(result: BackupResult, expected: NativeText) {
        val uri = fakeUri()
        coEvery { backupRepository.createBackupWithProgress(uri) } returns flowOf(
            BackupState.Completed(result)
        )

        sut.viewState.value.onBackupFileSelected(uri)

        assertFalse(sut.viewState.value.isBackupInProgress)
        assertEquals(expected, sut.viewState.value.lastBackupResult)
    }
}
