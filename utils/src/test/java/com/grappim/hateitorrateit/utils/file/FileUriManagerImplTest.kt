package com.grappim.hateitorrateit.utils.file

import androidx.core.content.FileProvider
import com.grappim.hateitorrateit.utils.HashUtils
import com.grappim.hateitorrateit.utils.ShadowFileProvider
import com.grappim.hateitorrateit.utils.models.CameraTakePictureData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(
    shadows = [ShadowFileProvider::class],
    manifest = Config.NONE
)
class FileUriManagerImplTest {

    private lateinit var fileUriManager: FileUriManager

    private val context = RuntimeEnvironment.getApplication()
    private val folderPathManager: FolderPathManager = mockk()
    private val hashUtils: HashUtils = mockk()
    private val fileInfoRetriever: FileInfoRetriever = mockk()
    private val fileCreationUtils: FileCreationUtils = mockk()

    @Before
    fun setUp() {
        fileUriManager = FileUriManagerImpl(
            context = context,
            folderPathManager = folderPathManager,
            hashUtils = hashUtils,
            fileInfoRetriever = fileInfoRetriever,
            fileCreationUtils = fileCreationUtils
        )
    }

    @Test
    fun `on getFileUriForTakePicture with isEdit false, should return CameraTakePictureData with data`() {
        val date = "2024-01-23_21-04-46"
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
    fun `on getFileUriForTakePicture with isEdit true, should return CameraTakePictureData with data`() {
        val date = "2024-01-23_21-04-46"
        val instant = Instant.now()
        val millis = instant.toEpochMilli()
        val fileName = "${date}_$millis.jpg"

        val tempFolderName = "folderName_temp"
        val folderName = "folderName"

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
