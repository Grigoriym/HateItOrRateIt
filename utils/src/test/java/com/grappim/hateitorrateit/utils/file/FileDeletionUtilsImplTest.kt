package com.grappim.hateitorrateit.utils.file

import com.grappim.hateitorrateit.utils.UriParser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE
)
class FileDeletionUtilsImplTest {

    private lateinit var fileDeletionUtils: FileDeletionUtils

    private val context = RuntimeEnvironment.getApplication()
    private val uriParser: UriParser = mockk()
    private val folderPathManager: FolderPathManager = mockk()

    @Before
    fun setUp() {
        fileDeletionUtils = FileDeletionUtilsImpl(
            context = context,
            ioDispatcher = UnconfinedTestDispatcher(),
            uriParser = uriParser,
            folderPathManager = folderPathManager
        )

        every { folderPathManager.getMainFolder(any()) } returns File(context.filesDir, "products")
    }

    @Test
    fun `on deleteFolder should delete the folder`() = runTest {
        val sourceFolder = File(context.filesDir, "products/folderName")
        assertTrue(sourceFolder.mkdirs())

        fileDeletionUtils.deleteFolder(sourceFolder.name)

        assertFalse(sourceFolder.exists())

        verify { folderPathManager.getMainFolder(sourceFolder.name) }
    }

    @Test
    fun `clearMainFolder should delete the main folder`() = runTest {
        val mainFolder = File(context.filesDir, "products")
        assertTrue(mainFolder.mkdirs())

        fileDeletionUtils.clearMainFolder()

        assertFalse(mainFolder.exists())

        verify { folderPathManager.getMainFolder("") }
    }
}
