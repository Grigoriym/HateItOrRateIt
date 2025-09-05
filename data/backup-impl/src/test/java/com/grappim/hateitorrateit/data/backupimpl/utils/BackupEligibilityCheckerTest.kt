package com.grappim.hateitorrateit.data.backupimpl.utils

import android.os.Build
import android.os.Environment
import com.grappim.hateitorrateit.testing.domain.getRandomString
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class BackupEligibilityCheckerTest {

    private val folderPathManager: FolderPathManager = mockk()

    private lateinit var sut: BackupEligibilityChecker

    @Before
    fun setUp() {
        sut = BackupEligibilityChecker(folderPathManager)
    }

    @Test
    @Config(
        sdk = [
            Build.VERSION_CODES.Q,
            Build.VERSION_CODES.R,
            Build.VERSION_CODES.S,
            Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
            Build.VERSION_CODES.VANILLA_ICE_CREAM
        ]
    )
    fun `on canCreateBackup for API more or equals than 29, returns true`() = runTest {
        val actual = sut.canCreateBackup()

        assertTrue(actual)
    }

    @Test
    @Config(
        sdk = [
            Build.VERSION_CODES.N,
            Build.VERSION_CODES.N_MR1,
            Build.VERSION_CODES.O,
            Build.VERSION_CODES.O_MR1,
            Build.VERSION_CODES.P
        ]
    )
    fun `on canCreateBackup for API less than 29, without errors, returns true`() = runTest {
        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val childFolder = getRandomString()
        every { folderPathManager.getBackupParentFolder() } returns file
        every { folderPathManager.getBackupChildFolderName() } returns childFolder

        val actual = sut.canCreateBackup()

        assertTrue(actual)
    }
}
