package com.grappim.hateitorrateit.utils.filesimpl.file.deletion

import android.content.Context
import androidx.core.content.FileProvider
import androidx.test.core.app.ApplicationProvider
import com.grappim.hateitorrateit.utils.filesapi.deletion.FileDeletionUtils
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import com.grappim.hateitorrateit.utils.filesapi.uri.UriParser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FileDeletionUtilsImplTest {

    private lateinit var fileDeletionUtils: FileDeletionUtils

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val uriParser: UriParser = mockk()
    private val folderPathManager: FolderPathManager = mockk()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        File(context.filesDir, "products").deleteRecursively()

        fileDeletionUtils = FileDeletionUtilsImpl(
            context = context,
            ioDispatcher = UnconfinedTestDispatcher(),
            uriParser = uriParser,
            folderPathManager = folderPathManager
        )

        every { folderPathManager.getMainFolder(any()) } returns File(context.filesDir, "products")
    }

    @Test
    fun on_deleteFile_by_uri_should_correctly_return_true() = runTest {
        val fileName = "testimage.jpg"
        val file = File(context.filesDir, "products/folder/$fileName").apply {
            parentFile?.mkdirs()
            createNewFile()
        }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val actual = fileDeletionUtils.deleteFile(uri)

        assertTrue(actual)
        assertFalse(file.exists())
    }

    @Test
    fun on_deleteFile_by_uri_string_should_correctly_return_true() = runTest {
        val fileName = "testimage.jpg"
        val file = File(context.filesDir, "products/folder/$fileName").apply {
            parentFile?.mkdirs()
            createNewFile()
        }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        every { uriParser.parse(any()) } returns uri

        val actual = fileDeletionUtils.deleteFile(uri.toString())

        verify { uriParser.parse(uri.toString()) }

        assertTrue(actual)
        assertFalse(file.exists())
    }

    @Test
    fun on_deleteFolder_should_delete_the_folder() = runTest {
        val sourceFolder = File(context.filesDir, "products/folderName")
        assertTrue(sourceFolder.mkdirs())

        fileDeletionUtils.deleteFolder(sourceFolder.name)

        assertFalse(sourceFolder.exists())

        verify { folderPathManager.getMainFolder(sourceFolder.name) }
    }

    @Test
    fun clearMainFolder_should_delete_the_main_folder() = runTest {
        val mainFolder = File(context.filesDir, "products")
        assertTrue(mainFolder.mkdirs())

        fileDeletionUtils.clearMainFolder()

        assertFalse(mainFolder.exists())

        verify { folderPathManager.getMainFolder("") }
    }
}
