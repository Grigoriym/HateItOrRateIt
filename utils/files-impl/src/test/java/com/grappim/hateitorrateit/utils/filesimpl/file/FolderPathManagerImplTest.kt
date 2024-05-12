package com.grappim.hateitorrateit.utils.filesimpl.file

import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import com.grappim.hateitorrateit.utils.filesimpl.file.pathmanager.FolderPathManagerImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE
)
class FolderPathManagerImplTest {

    private lateinit var folderPathManager: FolderPathManager

    private val context = RuntimeEnvironment.getApplication()

    @Before
    fun setUp() {
        folderPathManager = FolderPathManagerImpl(
            context = context
        )
    }

    @Test
    fun `getMainFolder should return correct folder`() {
        val expected = File(context.filesDir, "products")

        val actual = folderPathManager.getMainFolder()

        assertEquals(expected.path, actual.path)
    }

    @Test
    fun `getMainFolder with a child should return correct folder`() {
        val expected = File(context.filesDir, "products/testChild")

        val actual = folderPathManager.getMainFolder("testChild")

        assertEquals(expected.path, actual.path)
    }

    @Test
    fun `getTempFolderName should return correct temp folder string`() {
        val expected = "testFolder_temp"
        val actual = folderPathManager.getTempFolderName("testFolder")
        assertEquals(expected, actual)
    }

    @Test
    fun `getBackupFolderName should return correct backup folder string`() {
        val expected = "testFolder_backup"
        val actual = folderPathManager.getBackupFolderName("testFolder")
        assertEquals(expected, actual)
    }
}
