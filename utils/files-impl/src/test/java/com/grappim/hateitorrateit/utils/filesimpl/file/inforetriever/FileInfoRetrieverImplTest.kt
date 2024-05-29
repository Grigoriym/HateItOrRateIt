package com.grappim.hateitorrateit.utils.filesimpl.file.inforetriever

import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.grappim.hateitorrateit.testing.core.ShadowFileProvider
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import com.grappim.hateitorrateit.utils.filesapi.inforetriever.FileInfoRetriever
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import com.grappim.hateitorrateit.utils.filesimpl.MimeTypes
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.io.File
import java.time.Instant
import kotlin.test.assertFails
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(
    shadows = [ShadowFileProvider::class],
    manifest = Config.NONE
)
class FileInfoRetrieverImplTest {

    private lateinit var sut: FileInfoRetriever

    private val context = RuntimeEnvironment.getApplication()
    private val mimeTypes: MimeTypes = mockk()
    private val dateTimeUtils: DateTimeUtils = mockk()
    private val folderPathManager: FolderPathManager = mockk()

    private val jpgMimeType = "image/jpeg"
    private val pngMimeType = "image/png"

    @Before
    fun setUp() {
        sut = FileInfoRetrieverImpl(
            context = context,
            mimeTypes = mimeTypes,
            dateTimeUtils = dateTimeUtils,
            ioDispatcher = UnconfinedTestDispatcher(),
            folderPathManager = folderPathManager
        )

        shadowOf(MimeTypeMap.getSingleton()).addExtensionMimeTypeMapping("jpg", jpgMimeType)
        shadowOf(MimeTypeMap.getSingleton()).addExtensionMimeTypeMapping("png", pngMimeType)
    }

    @Test
    fun `on getFileExtension should return correct extension`() {
        every { mimeTypes.formatMimeType(any()) } returns jpgMimeType

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
    fun `on getMimeType should return correct mimeType of a file`() {
        val uriString = "https://grappim.com/products/tesimage.jpg"
        val parsed = Uri.parse(uriString)

        val result = sut.getMimeType(parsed)

        assertEquals(result, jpgMimeType)
    }

    @Test
    fun `on getFileSize should return correct size of a file`() {
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
    fun `on getFileName for uri should return correct name of a file`() {
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
    fun `on getFileName for extension should return correct fileName`() {
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
    fun `on getBitmapFileName should return correct name`() {
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
    fun `on findFileInFolder should return correct file`() = runTest {
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
    fun `on findFileInFolder throws error if no file found`() = runTest {
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
