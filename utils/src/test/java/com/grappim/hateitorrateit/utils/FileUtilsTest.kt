package com.grappim.hateitorrateit.utils

import android.net.Uri
import androidx.core.content.FileProvider
import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.utils.mappers.ImageDataMapper
import com.grappim.hateitorrateit.utils.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.models.ImageData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(
    shadows = [ShadowFileProvider::class],
    manifest = Config.NONE
)
class FileUtilsTest {

    private val hashUtils: HashUtils = mockk()
    private val dateTimeUtils: DateTimeUtils = mockk()
    private val imageDataMapper: ImageDataMapper = mockk()
    private val uriParser: UriParser = mockk()

    private lateinit var fileUtils: FileUtils

    private val context = RuntimeEnvironment.getApplication()

    @Before
    fun setup() {
        fileUtils = FileUtils(
            context = context,
            hashUtils = hashUtils,
            dateTimeUtils = dateTimeUtils,
            imageDataMapper = imageDataMapper,
            uriParser = uriParser,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun `prepareEditedImagesToPersist should return correct list of ProductImageData`() = runTest {
        val images = listOf(
            ImageData(
                1L,
                uri = Uri.parse("http://example.com/path_temp/image.jpg"),
                name = "image.jpg",
                size = 100L,
                mimeType = "image/jpeg",
                md5 = "abc123",
                isEdit = true
            ),
            ImageData(
                imageId = 2L,
                uri = Uri.parse("http://example.com/path/image2.jpg"),
                name = "image2.jpg",
                size = 200L,
                mimeType = "image/jpeg",
                md5 = "def456",
                isEdit = false
            )
        )

        val expectedProductImages = listOf(
            ProductImageData(
                imageId = 1L,
                name = "image.jpg",
                mimeType = "image/jpeg",
                uriPath = "/path/image.jpg",
                uriString = "http://example.com/path/image.jpg",
                size = 100L,
                md5 = "abc123",
                isEdit = true
            ),
            ProductImageData(
                imageId = 2L,
                name = "image2.jpg",
                mimeType = "image/jpeg",
                uriPath = "/path/image2.jpg",
                uriString = "http://example.com/path/image2.jpg",
                size = 200L,
                md5 = "def456",
                isEdit = false
            )
        )

        images.forEach { image ->
            coEvery {
                imageDataMapper.toProductImageData(image)
            } returns ProductImageData(
                imageId = image.imageId,
                name = image.name,
                mimeType = image.mimeType,
                uriPath = image.uri.path ?: "",
                uriString = image.uri.toString(),
                size = image.size,
                md5 = image.md5,
                isEdit = image.isEdit
            )
        }

        val actual = fileUtils.prepareEditedImagesToPersist(images)

        assertEquals(expectedProductImages, actual)
        images.forEach { image ->
            coVerify { imageDataMapper.toProductImageData(image) }
        }
    }

    @Test
    fun `on moveSourceFilesToDestinationFolder should correctly move files from source to destination and delete source folder`() =
        runTest {
            val sourceFolder = File(context.filesDir, "products/source")
            assertTrue(sourceFolder.mkdirs())

            val file1 = File(sourceFolder, "testFile1.jpg").apply { createNewFile() }
            val file2 = File(sourceFolder, "testFile2.jpg").apply { createNewFile() }

            val targetFolder = File(context.filesDir, "products/destination")
            assertTrue(targetFolder.mkdirs())

            fileUtils.moveSourceFilesToDestinationFolder(
                sourceFolder.name,
                targetFolder.name
            )

            val targetFiles = targetFolder.list() ?: emptyArray()
            assertTrue(targetFiles.isNotEmpty())
            assertTrue(targetFiles.contains(file1.name))
            assertTrue(targetFiles.contains(file2.name))

            assertFalse(sourceFolder.exists())
            assertFalse(file1.exists())
            assertFalse(file2.exists())
        }

    @Test
    fun `on copySourceFilesToDestination should copy source folder contents to destination folder`() =
        runTest {
            val sourceFolder = File(context.filesDir, "products/source")
            assertTrue(sourceFolder.mkdirs())

            val file1 = File(sourceFolder, "testFile1.jpg").apply { createNewFile() }
            val file2 = File(sourceFolder, "testFile2.jpg").apply { createNewFile() }

            val targetFolder = File(context.filesDir, "products/destination")
            assertTrue(targetFolder.mkdirs())

            fileUtils.copySourceFilesToDestination(
                sourceFolder.name,
                targetFolder.name
            )

            val targetFiles = targetFolder.list() ?: emptyArray()
            assertTrue(targetFiles.isNotEmpty())
            assertTrue(targetFiles.contains(file1.name))
            assertTrue(targetFiles.contains(file2.name))

            assertTrue(sourceFolder.exists())
            assertTrue(file1.exists())
            assertTrue(file2.exists())
        }

    @Test
    fun `on getFileUriForTakePicture with isEdit false, should return CameraTakePictureData with data`() {
        val date = "2024-01-23_21-04-46"
        val instant = Instant.now()
        val millis = instant.toEpochMilli()
        every { dateTimeUtils.formatToDocumentFolder(any()) } returns date
        every { dateTimeUtils.getInstantNow() } returns instant

        val sourceFolder = File(context.filesDir, "products/folderName")
        assertTrue(sourceFolder.mkdirs())

        val file = File(sourceFolder, "${date}_$millis.jpg").apply { createNewFile() }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val expected = CameraTakePictureData(
            uri = uri,
            file = file
        )

        val actual = fileUtils.getFileUriForTakePicture(
            folderName = "folderName",
            isEdit = false
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `on getFileUriForTakePicture with isEdit true, should return CameraTakePictureData with data`() {
        val date = "2024-01-23_21-04-46"
        val instant = Instant.now()
        val millis = instant.toEpochMilli()
        every { dateTimeUtils.formatToDocumentFolder(any()) } returns date
        every { dateTimeUtils.getInstantNow() } returns instant

        val sourceFolder = File(context.filesDir, "products/folderName_temp")
        assertTrue(sourceFolder.mkdirs())

        val file = File(sourceFolder, "${date}_$millis.jpg").apply { createNewFile() }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val expected = CameraTakePictureData(
            uri = uri,
            file = file
        )

        val actual = fileUtils.getFileUriForTakePicture(
            folderName = "folderName",
            isEdit = true
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `on deleteFolder should delete the folder`() = runTest {
        val sourceFolder = File(context.filesDir, "products/folderName")
        assertTrue(sourceFolder.mkdirs())

        fileUtils.deleteFolder(sourceFolder.name)

        assertFalse(sourceFolder.exists())
    }

    @Test
    fun `getMainFolder should return correct folder`() {
        val expected = File(context.filesDir, "products")

        val actual = fileUtils.getMainFolder()

        assertEquals(expected.path, actual.path)
    }

    @Test
    fun `getMainFolder with a child should return correct folder`() {
        val expected = File(context.filesDir, "products/testChild")

        val actual = fileUtils.getMainFolder("testChild")

        assertEquals(expected.path, actual.path)
    }

    @Test
    fun `clearMainFolder should delete the main folder`() = runTest {
        val mainFolder = File(context.filesDir, "products")
        assertTrue(mainFolder.mkdirs())

        fileUtils.clearMainFolder()

        assertFalse(mainFolder.exists())
    }

    @Test
    fun `getTempFolderName should return correct temp folder string`() {
        val expected = "testFolder_temp"
        val actual = fileUtils.getTempFolderName("testFolder")
        assertEquals(expected, actual)
    }

    @Test
    fun `getBackupFolderName should return correct backup folder string`() {
        val expected = "testFolder_backup"
        val actual = fileUtils.getBackupFolderName("testFolder")
        assertEquals(expected, actual)
    }
}
