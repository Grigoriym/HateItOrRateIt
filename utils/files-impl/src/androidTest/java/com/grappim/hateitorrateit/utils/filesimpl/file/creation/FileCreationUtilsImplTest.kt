package com.grappim.hateitorrateit.utils.filesimpl.file.creation

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.grappim.hateitorrateit.testing.domain.getRandomString
import com.grappim.hateitorrateit.utils.filesapi.creation.FileCreationUtils
import com.grappim.hateitorrateit.utils.filesapi.inforetriever.FileInfoRetriever
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class FileCreationUtilsImplTest {

    private lateinit var fileCreationUtils: FileCreationUtils

    private val fileInfoRetriever: FileInfoRetriever = mockk()
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val folderPathManager: FolderPathManager = mockk()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        fileCreationUtils = FileCreationUtilsImpl(
            fileInfoRetriever = fileInfoRetriever,
            context = context,
            folderPathManager = folderPathManager,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun on_createFileLocally_should_correctly_create_a_new_file_from_uri() = runTest {
        val content = getRandomString().toByteArray()
        val sourceFile = File(context.filesDir, "source/${getRandomString()}").apply {
            parentFile?.mkdirs()
            writeBytes(content)
        }
        val uri: Uri = Uri.fromFile(sourceFile)
        val folderName = "testFolder"
        val fileName = getRandomString()
        val extension = getRandomString()

        val mainFolderFile = File(context.filesDir, "products/$folderName").apply { mkdirs() }

        every { fileInfoRetriever.getFileExtension(any()) } returns extension
        every { folderPathManager.getMainFolder(any()) } returns mainFolderFile
        every { fileInfoRetriever.getFileName(extension = any()) } returns fileName

        val actual = fileCreationUtils.createFileLocally(uri, folderName)

        verify { fileInfoRetriever.getFileExtension(uri) }
        verify { folderPathManager.getMainFolder(folderName) }
        verify { fileInfoRetriever.getFileName(extension = extension) }

        val expected = File(context.filesDir, "products/$folderName/$fileName")

        assertEquals(expected, actual)
        assertEquals(content.toList(), actual.readBytes().toList())
    }
}
