package com.grappim.hateitorrateit.feature.settings.ui.screen.backup

import android.content.Intent
import app.cash.turbine.test
import com.grappim.hateitorrateit.data.backupapi.BackupRepository
import com.grappim.hateitorrateit.data.backupapi.ImportRepository
import com.grappim.hateitorrateit.data.backupapi.models.ImportMode
import com.grappim.hateitorrateit.testing.core.MainDispatcherRule
import com.grappim.hateitorrateit.testing.core.getRandomUri
import com.grappim.hateitorrateit.utils.androidapi.IntentGenerator
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class SettingsBackupViewModelRobolectricTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val backupRepository: BackupRepository = mockk()
    private val importRepository: ImportRepository = mockk()
    private val intentGenerator: IntentGenerator = mockk()

    lateinit var sut: SettingsBackupViewModel

    @Before
    fun setup() {
        sut = SettingsBackupViewModel(
            backupRepository = backupRepository,
            importRepository = importRepository,
            intentGenerator = intentGenerator
        )
    }

    @Test
    fun `on openDownloadsFolder should send correct intent`() = runTest {
        val intent = Intent(Intent.ACTION_VIEW)
        every { intentGenerator.generateOpenDownloadsFolderIntent() } returns intent

        sut.intentAction.test {
            sut.viewState.value.onOpenDownloadsFolder()

            assertEquals(awaitItem(), intent)
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `on onFileSelected should update correct data`() = runTest {
        val uri = getRandomUri()
        sut.viewState.value.onFileSelected(uri)

        assertEquals(sut.viewState.value.selectedBackupFileUri, uri)
        assertTrue(sut.viewState.value.shouldShowImportModeDialog)
        assertFalse(sut.viewState.value.shouldShowFilePicker)
    }

    @Test
    fun `on onImportModeSelected with selectedBackupFileUri as null should do nothing`() = runTest {
        val importMode = ImportMode.CREATE_NEW
        sut.viewState.value.onImportModeSelected(importMode)

        assertFalse(sut.viewState.value.isImportInProgress)
    }
}
