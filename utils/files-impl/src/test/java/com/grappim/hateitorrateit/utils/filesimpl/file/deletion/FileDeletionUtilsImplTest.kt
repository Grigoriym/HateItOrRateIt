package com.grappim.hateitorrateit.utils.filesimpl.file.deletion

import androidx.core.content.FileProvider
import com.grappim.hateitorrateit.utils.filesapi.deletion.FileDeletionUtils
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import com.grappim.hateitorrateit.utils.filesapi.uri.UriParser
import com.grappim.hateitorrateit.utils.filesimpl.ShadowFileProvider
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
    shadows = [ShadowFileProvider::class],
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
    fun `on deleteFile by uri should correctly return true`() = runTest {
        val fileName = "testimage.jpg"
        val file = File(context.filesDir, "products/folder/$fileName")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val actual = fileDeletionUtils.deleteFile(uri)

        assertTrue(actual)
    }

    @Test
    fun `on deleteFile by uri string should correctly return true`() = runTest {
        val fileName = "testimage.jpg"
        val file = File(context.filesDir, "products/folder/$fileName")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        every { uriParser.parse(any()) } returns uri

        val actual = fileDeletionUtils.deleteFile(uri.toString())

        verify { uriParser.parse(uri.toString()) }

        assertTrue(actual)
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
