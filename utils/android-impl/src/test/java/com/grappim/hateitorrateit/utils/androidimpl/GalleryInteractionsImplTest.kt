package com.grappim.hateitorrateit.utils.androidimpl

import android.content.ContentResolver
import android.os.Build
import android.os.Environment
import com.grappim.hateitorrateit.testing.core.ShadowFileProvider
import com.grappim.hateitorrateit.testing.core.getRandomFile
import com.grappim.hateitorrateit.testing.core.getUriForFile
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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowContentResolver
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [
        Build.VERSION_CODES.N,
        Build.VERSION_CODES.O_MR1,
        Build.VERSION_CODES.P,
        Build.VERSION_CODES.Q,
        Build.VERSION_CODES.R,
        Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    ],
    shadows = [ShadowFileProvider::class]
)
class GalleryInteractionsImplTest {

    private val context = RuntimeEnvironment.getApplication()

    private val fileInfoRetriever: FileInfoRetriever = mockk()
    private val uriParser: UriParser = mockk()
    private val fileTransferOperations: FileTransferOperations = mockk()

    private lateinit var contentResolver: ContentResolver
    private lateinit var shadowContentResolver: ShadowContentResolver

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
        contentResolver = context.contentResolver
        shadowContentResolver = Shadows.shadowOf(contentResolver)
    }

    @Test
    @Config(
        sdk = [
            Build.VERSION_CODES.P,
            Build.VERSION_CODES.O_MR1,
            Build.VERSION_CODES.N
        ]
    )
    fun `on saveImageInGallery for API level less than 29 return Success`() = runTest {
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
    @Config(sdk = [Build.VERSION_CODES.P])
    fun `on saveImageInGallery for API level less than 29 with NoSuchFileException returns Failure`() =
        runTest {
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
    @Config(sdk = [Build.VERSION_CODES.P])
    fun `on saveImageInGallery for API level less than 29 with FileAlreadyExistsException returns Failure`() =
        runTest {
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
    @Config(sdk = [Build.VERSION_CODES.P])
    fun `on saveImageInGallery for API level less than 29 with IOException returns Failure`() =
        runTest {
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
    @Config(
        sdk = [
            Build.VERSION_CODES.Q,
            Build.VERSION_CODES.R,
            Build.VERSION_CODES.UPSIDE_DOWN_CAKE
        ]
    )
    fun `on saveImageInGallery for API level more than 28 return Success`() = runTest {
        val content = getRandomString().toByteArray()
        val fileDataByteArray = ByteArrayInputStream(content)
        val fos = ByteArrayOutputStream(1000)

        val name = "image.jpg"
        val folderName = "MyFolder"
        val uriString = "uri"
        val mimeType = "image/jpeg"

        val file = File(context.filesDir, "products/folder/$name")

        val uri = context.getUriForFile(file)

        every { uriParser.parse(uriString) } returns uri

        shadowContentResolver.registerInputStream(uri, fileDataByteArray)
        shadowContentResolver.registerOutputStream(uri, fos)

        val result = sut.saveImageInGallery(uriString, name, mimeType, folderName)

        assertTrue(result.isSuccess)
    }

    private fun getPicturesDirectory() =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
}
