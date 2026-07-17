package com.grappim.hateitorrateit.utils.androidimpl

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.test.core.app.ApplicationProvider
import com.grappim.hateitorrateit.testing.core.getRandomFile
import com.grappim.hateitorrateit.testing.domain.getRandomString
import com.grappim.hateitorrateit.utils.androidapi.GalleryInteractions
import com.grappim.hateitorrateit.utils.filesapi.inforetriever.FileInfoRetriever
import com.grappim.hateitorrateit.utils.filesapi.transfer.FileTransferOperations
import com.grappim.hateitorrateit.utils.filesapi.uri.UriParser
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.IOException
import kotlin.test.assertTrue

class GalleryInteractionsImplTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val fileInfoRetriever: FileInfoRetriever = mockk()
    private val uriParser: UriParser = mockk()
    private val fileTransferOperations: FileTransferOperations = mockk()

    private lateinit var sut: GalleryInteractions

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        sut = GalleryInteractionsImpl(
            ioDispatcher = UnconfinedTestDispatcher(),
            context = context,
            fileInfoRetriever = fileInfoRetriever,
            uriParser = uriParser,
            fileTransferOperations = fileTransferOperations
        )
    }

    @Test
    fun on_saveImageInGallery_for_API_level_less_than_29_return_Success() = runTest {
        assumeTrue(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)

        val name = getRandomString()
        val folderName = getRandomString()
        val sourceFile = context.getRandomFile()

        val targetDirectory = getPicturesDirectory()
        val targetDirFile =
            File(targetDirectory, GalleryInteractionsImpl.GALLERY_FOLDER_NAME).apply { mkdirs() }

        val targetFile = File(targetDirFile, name).apply { mkdirs() }

        coEvery { fileInfoRetriever.findFileInFolder(name, folderName) } returns sourceFile

        coEvery {
            fileTransferOperations.writeSourceFileToTargetFile(any(), any())
        } just Runs

        val result = sut.saveImageInGallery("", name, "", folderName)

        assertTrue(result.isSuccess)

        coVerify {
            fileInfoRetriever.findFileInFolder(name, folderName)
            fileTransferOperations.writeSourceFileToTargetFile(sourceFile, targetFile)
        }
    }

    @Test
    fun on_saveImageInGallery_for_API_level_less_than_29_with_NoSuchFileException_returns_Failure() = runTest {
        assumeTrue(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)

        val name = getRandomString()
        val folderName = getRandomString()
        val sourceFile = context.getRandomFile()

        val targetDirectory = getPicturesDirectory()
        val targetDirFile =
            File(
                targetDirectory,
                GalleryInteractionsImpl.GALLERY_FOLDER_NAME
            ).apply { mkdirs() }
        val targetFile = File(targetDirFile, name).apply { mkdirs() }

        coEvery { fileInfoRetriever.findFileInFolder(name, folderName) } returns sourceFile

        coEvery {
            fileTransferOperations.writeSourceFileToTargetFile(any(), any())
        } throws NoSuchFileException(File(""))

        val result = sut.saveImageInGallery("", name, "", folderName)

        assertTrue(result.isFailure)

        coVerify {
            fileInfoRetriever.findFileInFolder(name, folderName)
            fileTransferOperations.writeSourceFileToTargetFile(sourceFile, targetFile)
        }
    }

    @Test
    fun on_saveImageInGallery_for_API_level_less_than_29_with_FileAlreadyExistsException_returns_Failure() = runTest {
        assumeTrue(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)

        val name = getRandomString()
        val folderName = getRandomString()
        val sourceFile = context.getRandomFile()

        val targetDirectory = getPicturesDirectory()
        val targetDirFile =
            File(
                targetDirectory,
                GalleryInteractionsImpl.GALLERY_FOLDER_NAME
            ).apply { mkdirs() }
        val targetFile = File(targetDirFile, name).apply { mkdirs() }

        coEvery { fileInfoRetriever.findFileInFolder(name, folderName) } returns sourceFile

        coEvery {
            fileTransferOperations.writeSourceFileToTargetFile(any(), any())
        } throws FileAlreadyExistsException(File(""))

        val result = sut.saveImageInGallery("", name, "", folderName)

        assertTrue(result.isFailure)

        coVerify {
            fileInfoRetriever.findFileInFolder(name, folderName)
            fileTransferOperations.writeSourceFileToTargetFile(sourceFile, targetFile)
        }
    }

    @Test
    fun on_saveImageInGallery_for_API_level_less_than_29_with_IOException_returns_Failure() = runTest {
        assumeTrue(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)

        val name = getRandomString()
        val folderName = getRandomString()
        val sourceFile = context.getRandomFile()

        val targetDirectory = getPicturesDirectory()
        val targetDirFile =
            File(
                targetDirectory,
                GalleryInteractionsImpl.GALLERY_FOLDER_NAME
            ).apply { mkdirs() }
        val targetFile = File(targetDirFile, name).apply { mkdirs() }

        coEvery { fileInfoRetriever.findFileInFolder(name, folderName) } returns sourceFile

        coEvery {
            fileTransferOperations.writeSourceFileToTargetFile(any(), any())
        } throws IOException()

        val result = sut.saveImageInGallery("", name, "", folderName)

        assertTrue(result.isFailure)

        coVerify {
            fileInfoRetriever.findFileInFolder(name, folderName)
            fileTransferOperations.writeSourceFileToTargetFile(sourceFile, targetFile)
        }
    }

    @Test
    fun on_saveImageInGallery_for_API_level_more_than_28_return_Success() = runTest {
        assumeTrue(Build.VERSION.SDK_INT > Build.VERSION_CODES.P)

        val content = getRandomString().toByteArray()

        val name = "image.jpg"
        val folderName = "MyFolder"
        val uriString = "uri"
        val mimeType = "image/jpeg"

        val file = File(context.filesDir, "products/folder/$name").apply {
            parentFile?.mkdirs()
            writeBytes(content)
        }

        val uri: Uri = Uri.fromFile(file)

        every { uriParser.parse(uriString) } returns uri

        val result = sut.saveImageInGallery(uriString, name, mimeType, folderName)

        assertTrue(result.isSuccess)
    }

    private fun getPicturesDirectory(): File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
}
