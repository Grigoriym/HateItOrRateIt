package com.grappim.hateitorrateit.utils.filesimpl.file.urimanager

import android.content.Context
import androidx.core.content.FileProvider
import androidx.test.core.app.ApplicationProvider
import com.grappim.hateitorrateit.testing.domain.getRandomLong
import com.grappim.hateitorrateit.testing.domain.getRandomString
import com.grappim.hateitorrateit.utils.filesapi.creation.FileCreationUtils
import com.grappim.hateitorrateit.utils.filesapi.inforetriever.FileInfoRetriever
import com.grappim.hateitorrateit.utils.filesapi.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.filesapi.models.ProductImageUIData
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import com.grappim.hateitorrateit.utils.filesapi.urimanager.FileUriManager
import com.grappim.hateitorrateit.utils.filesimpl.HashUtils
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.File
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FileUriManagerImplTest {

    private lateinit var fileUriManager: FileUriManager

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val folderPathManager: FolderPathManager = mockk()
    private val hashUtils: HashUtils = mockk()
    private val fileInfoRetriever: FileInfoRetriever = mockk()
    private val fileCreationUtils: FileCreationUtils = mockk()

    @Before
    fun setUp() {
        File(context.filesDir, "products").deleteRecursively()

        fileUriManager = FileUriManagerImpl(
            context = context,
            folderPathManager = folderPathManager,
            hashUtils = hashUtils,
            fileInfoRetriever = fileInfoRetriever,
            fileCreationUtils = fileCreationUtils
        )
    }

    @Test
    fun on_getFileUriFromGalleryUriwith_isEdit_true_should_return_correct_ImageData_with_temp_folder_name() = runTest {
        val fileName = "testimage.jpg"
        val folder = getRandomString()
        val tempFolder = "${folder}_temp"
        val file = File(context.filesDir, "products/folder/$fileName")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val size = getRandomLong()
        val mimeType = "image/jpeg"
        val md5 = getRandomString()

        every { folderPathManager.getTempFolderName(any()) } returns tempFolder
        coEvery { fileCreationUtils.createFileLocally(any(), any()) } returns file
        every { fileInfoRetriever.getFileSize(any()) } returns size
        every { fileInfoRetriever.getMimeType(any()) } returns mimeType
        every { hashUtils.md5(file) } returns md5

        val actual = fileUriManager.getFileUriFromGalleryUri(
            uri = uri,
            folderName = folder,
            isEdit = true
        )

        verify { folderPathManager.getTempFolderName(folder) }
        coVerify { fileCreationUtils.createFileLocally(uri, tempFolder) }
        verify { fileInfoRetriever.getFileSize(uri) }
        verify { fileInfoRetriever.getMimeType(uri) }
        verify { hashUtils.md5(file) }

        val expected = ProductImageUIData(
            uri = uri,
            name = file.name,
            size = size,
            mimeType = mimeType,
            md5 = md5,
            isEdit = true
        )
        assertEquals(expected, actual)
    }

    @Test
    fun on_getFileUriFromGalleryUri_with_isEdit_false_should_return_correct_ImageData() = runTest {
        val fileName = "testimage.jpg"
        val folder = getRandomString()
        val file = File(context.filesDir, "products/folder/$fileName")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val size = getRandomLong()
        val mimeType = "image/jpeg"
        val md5 = getRandomString()

        coEvery { fileCreationUtils.createFileLocally(any(), any()) } returns file
        every { fileInfoRetriever.getFileSize(any()) } returns size
        every { fileInfoRetriever.getMimeType(any()) } returns mimeType
        every { hashUtils.md5(file) } returns md5

        val actual = fileUriManager.getFileUriFromGalleryUri(
            uri = uri,
            folderName = folder,
            isEdit = false
        )

        coVerify { fileCreationUtils.createFileLocally(uri, folder) }
        verify { fileInfoRetriever.getFileSize(uri) }
        verify { fileInfoRetriever.getMimeType(uri) }
        verify { hashUtils.md5(file) }

        val expected = ProductImageUIData(
            uri = uri,
            name = file.name,
            size = size,
            mimeType = mimeType,
            md5 = md5,
            isEdit = false
        )
        assertEquals(expected, actual)
    }

    @Test
    fun on_getFileUriForTakePicture_with_isEdit_false_should_return_CameraTakePictureData_with_data() {
        val date = getRandomString()
        val instant = Instant.now()
        val millis = instant.toEpochMilli()
        val folderName = "folderName"
        val fileName = "${date}_$millis.jpg"

        val sourceFolder = File(context.filesDir, "products/$folderName")
        assertTrue(sourceFolder.mkdirs())

        val file = File(sourceFolder, fileName).apply { createNewFile() }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val expected = CameraTakePictureData(
            uri = uri,
            file = file
        )

        every { fileInfoRetriever.getBitmapFileName() } returns fileName
        every { folderPathManager.getMainFolder(any()) } returns File(
            context.filesDir,
            "products/$folderName"
        )

        val actual = fileUriManager.getFileUriForTakePicture(
            folderName = folderName,
            isEdit = false
        )

        assertEquals(expected, actual)

        verify { fileInfoRetriever.getBitmapFileName() }
        verify { folderPathManager.getMainFolder(folderName) }
    }

    @Test
    fun on_getFileDataFromCameraPicture_should_return_correct_ImageData() {
        val fileName = "testimage.jpg"
        val file = File(context.filesDir, "products/folder/$fileName")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val size = getRandomLong()
        val mimeType = "image/jpeg"
        val md5 = getRandomString()

        val data = CameraTakePictureData(uri, file)

        every { fileInfoRetriever.getFileSize(any()) } returns size
        every { fileInfoRetriever.getMimeType(any()) } returns mimeType
        every { hashUtils.md5(any()) } returns md5
        every { fileInfoRetriever.getFileName(uri = any()) } returns fileName

        val actual = fileUriManager.getFileDataFromCameraPicture(data, false)

        verify { fileInfoRetriever.getFileSize(uri) }
        verify { fileInfoRetriever.getMimeType(uri) }
        verify { hashUtils.md5(file) }
        verify { fileInfoRetriever.getFileName(uri) }

        val expected = ProductImageUIData(
            uri = uri,
            name = fileName,
            size = size,
            mimeType = mimeType,
            md5 = md5,
            isEdit = false
        )

        assertEquals(expected, actual)
    }

    @Test
    fun on_getFileUriForTakePicture_with_isEdit_true_should_return_CameraTakePictureData_with_data() {
        val date = getRandomString()
        val instant = Instant.now()
        val millis = instant.toEpochMilli()
        val fileName = "${date}_$millis.jpg"

        val tempFolderName = "folderName_temp"
        val folderName = getRandomString()

        val sourceFolder = File(context.filesDir, "products/$tempFolderName")
        assertTrue(sourceFolder.mkdirs())

        val file = File(sourceFolder, fileName).apply { createNewFile() }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val expected = CameraTakePictureData(
            uri = uri,
            file = file
        )

        every { folderPathManager.getTempFolderName(any()) } returns tempFolderName
        every { fileInfoRetriever.getBitmapFileName() } returns fileName
        every { folderPathManager.getMainFolder(any()) } returns File(
            context.filesDir,
            "products/$tempFolderName"
        )

        val actual = fileUriManager.getFileUriForTakePicture(
            folderName = folderName,
            isEdit = true
        )

        assertEquals(expected, actual)

        verify { folderPathManager.getTempFolderName(folderName) }
        verify { fileInfoRetriever.getBitmapFileName() }
        verify { folderPathManager.getMainFolder(tempFolderName) }
    }
}
