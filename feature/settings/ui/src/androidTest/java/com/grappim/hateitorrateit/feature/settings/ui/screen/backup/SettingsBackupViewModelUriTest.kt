package com.grappim.hateitorrateit.feature.settings.ui.screen.backup

import com.grappim.hateitorrateit.data.backupapi.BackupRepository
import com.grappim.hateitorrateit.data.backupapi.ImportRepository
import com.grappim.hateitorrateit.data.backupapi.models.ImportMode
import com.grappim.hateitorrateit.testing.core.MainDispatcherRule
import com.grappim.hateitorrateit.testing.core.getRandomUri
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsBackupViewModelUriTest {

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
    fun on_onFileSelected_should_update_correct_data() = runTest {
        val uri = getRandomUri()
        sut.viewState.value.onFileSelected(uri)

        assertEquals(sut.viewState.value.selectedBackupFileUri, uri)
        assertTrue(sut.viewState.value.shouldShowImportModeDialog)
        assertFalse(sut.viewState.value.shouldShowFilePicker)
    }

    @Test
    fun on_onImportModeSelected_with_selectedBackupFileUri_as_null_should_do_nothing() = runTest {
        val importMode = ImportMode.CREATE_NEW
        sut.viewState.value.onImportModeSelected(importMode)

        assertFalse(sut.viewState.value.isImportInProgress)
    }
}
