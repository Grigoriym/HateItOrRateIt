package com.grappim.hateitorrateit.utils.filesimpl.file.inforetriever

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.test.core.app.ApplicationProvider
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import com.grappim.hateitorrateit.utils.filesapi.inforetriever.FileInfoRetriever
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import com.grappim.hateitorrateit.utils.filesimpl.MimeTypesMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File
import java.time.Instant
import kotlin.test.assertFails
import kotlin.test.assertTrue

class FileInfoRetrieverImplTest {

    private lateinit var sut: FileInfoRetriever

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val mimeTypesMapper: MimeTypesMapper = mockk()
    private val dateTimeUtils: DateTimeUtils = mockk()
    private val folderPathManager: FolderPathManager = mockk()

    private val jpgMimeType = "image/jpeg"

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        sut = FileInfoRetrieverImpl(
            context = context,
            mimeTypesMapper = mimeTypesMapper,
            dateTimeUtils = dateTimeUtils,
            ioDispatcher = UnconfinedTestDispatcher(),
            folderPathManager = folderPathManager
        )
    }

    @Test
    fun on_getFileExtension_should_return_correct_extension() {
        every { mimeTypesMapper.formatMimeType(any()) } returns jpgMimeType

        val file = File(context.filesDir, "testimage.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val actual = sut.getFileExtension(uri)

        assertEquals(jpgMimeType, actual)
    }

    @Test
    fun on_getMimeType_should_return_correct_mimeType_of_a_file() {
        val uriString = "https://grappim.com/products/tesimage.jpg"
        val parsed = Uri.parse(uriString)

        val result = sut.getMimeType(parsed)

        assertEquals(result, jpgMimeType)
    }

    @Test
    fun on_getFileSize_should_return_correct_size_of_a_file() {
        val content = "sdgsd sdgsd gsd gdsgsdg".toByteArray()
        val expected = content.size.toLong()

        val file = File(context.filesDir, "testFile.txt").apply {
            writeBytes(content)
        }

        val uri = Uri.fromFile(file)

        val actual = sut.getFileSize(uri)
        assertEquals(expected, actual)
    }

    @Test
    fun on_getFileName_for_uri_should_return_correct_name_of_a_file() {
        val expected = "testimage.jpg"
        val file = File(context.filesDir, expected)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val actual = sut.getFileName(uri)
        assertEquals(expected, actual)
    }

    @Test
    fun on_getFileName_for_extension_should_return_correct_fileName() {
        val extension = "png"
        val stringDate = "2024-03-06_12-12-23"
        val millis = 123234L
        val expected = "${stringDate}_$millis.$extension"
        val instant = Instant.ofEpochMilli(millis)
        every { dateTimeUtils.formatToDocumentFolder(any()) } returns stringDate
        every { dateTimeUtils.getInstantNow() } returns instant

        val actual = sut.getFileName(extension)
        assertEquals(expected, actual)
    }

    @Test
    fun on_getBitmapFileName_should_return_correct_name() {
        val stringDate = "2024-03-06_12-12-23"
        val millis = 123234L
        val expected = "${stringDate}_$millis.jpg"
        val instant = Instant.ofEpochMilli(millis)
        every { dateTimeUtils.formatToDocumentFolder(any()) } returns stringDate
        every { dateTimeUtils.getInstantNow() } returns instant

        val actual = sut.getBitmapFileName()
        assertEquals(expected, actual)
    }

    @Test
    fun on_findFileInFolder_should_return_correct_file() = runTest {
        val folderName = "testFolder"
        val fileName = "testName"

        val mainFolderFile = File(context.filesDir, "products/$folderName").apply { mkdirs() }
        assertTrue(mainFolderFile.exists())
        assertTrue(mainFolderFile.isDirectory)

        val expected = File(context.filesDir, "products/$folderName/$fileName")
            .apply { createNewFile() }
        assertTrue(expected.exists())
        assertTrue(expected.isFile)

        every { folderPathManager.getMainFolder(any()) } returns mainFolderFile

        val actual = sut.findFileInFolder(fileName, folderName)

        verify { folderPathManager.getMainFolder(folderName) }
        assertTrue(mainFolderFile.exists())
        assertEquals(expected, actual)
    }

    @Test
    fun on_findFileInFolder_throws_error_if_no_file_found() = runTest {
        val folderName = "testFolder"
        val fileName = "testName"
        val otherFileName = "otherFileName"

        val mainFolderFile = File(context.filesDir, "products/$folderName").apply { mkdirs() }
        assertTrue(mainFolderFile.exists())
        assertTrue(mainFolderFile.isDirectory)

        val expected = File(context.filesDir, "products/$folderName/$fileName")
            .apply { createNewFile() }
        assertTrue(expected.exists())
        assertTrue(expected.isFile)

        every { folderPathManager.getMainFolder(any()) } returns mainFolderFile

        assertFails { sut.findFileInFolder(otherFileName, folderName) }

        verify { folderPathManager.getMainFolder(folderName) }
    }
}
