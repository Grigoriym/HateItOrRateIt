package com.grappim.hateitorrateit.feature.settings.ui.screen.backup

import com.grappim.hateitorrateit.data.backupapi.BackupRepository
import com.grappim.hateitorrateit.data.backupapi.ImportRepository
import com.grappim.hateitorrateit.testing.core.MainDispatcherRule
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
}
