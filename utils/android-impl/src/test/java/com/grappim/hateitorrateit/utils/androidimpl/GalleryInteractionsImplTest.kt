package com.grappim.hateitorrateit.utils.androidimpl

import android.content.ContentResolver
import android.os.Environment
import androidx.core.content.FileProvider
import com.grappim.hateitorrateit.testing.core.ShadowFileProvider
import com.grappim.hateitorrateit.testing.domain.getRandomString
import com.grappim.hateitorrateit.utils.androidapi.GalleryInteractions
import com.grappim.hateitorrateit.utils.androidapi.SaveImageState
import com.grappim.hateitorrateit.utils.filesapi.inforetriever.FileInfoRetriever
import com.grappim.hateitorrateit.utils.filesapi.transfer.FileTransferOperations
import com.grappim.hateitorrateit.utils.filesapi.uri.UriParser
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
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

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [24, 27, 28, 29, 30],
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
    @Config(sdk = [28, 27, 24])
    fun `on saveImageInGallery for API level less than 29 return success`() = runTest {
        val name = "image.jpg"
        val folderName = "MyFolder"
        val sourceFile = File(context.filesDir, "products/$folderName/srcFile").apply { mkdirs() }

        val targetDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val targetDirFile = File(targetDirectory, "HateItOrRateIt").apply { mkdirs() }

        val targetFile = File(targetDirFile, name).apply { mkdirs() }

        coEvery { fileInfoRetriever.findFileInFolder(name, folderName) } returns sourceFile

        coEvery {
            fileTransferOperations.writeSourceFileToTargetFile(any(), any())
        } just Runs

        val result = sut.saveImageInGallery("", name, "", folderName)

        assert(result is SaveImageState.Success)

        coVerify {
            fileInfoRetriever.findFileInFolder(name, folderName)
            fileTransferOperations.writeSourceFileToTargetFile(sourceFile, targetFile)
        }
    }

    @Test
    @Config(sdk = [29, 30])
    fun `on saveImageInGallery for API level more than 28 return success`() = runTest {
        val content = getRandomString().toByteArray()
        val fileDataByteArray = ByteArrayInputStream(content)
        val fos = ByteArrayOutputStream(1000)

        val name = "image.jpg"
        val folderName = "MyFolder"
        val uriString = "uri"
        val mimeType = "image/jpeg"

        val file = File(context.filesDir, "products/folder/$name")

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        every { uriParser.parse(uriString) } returns uri

        shadowContentResolver.registerInputStream(uri, fileDataByteArray)
        shadowContentResolver.registerOutputStream(uri, fos)

        val result = sut.saveImageInGallery(uriString, name, mimeType, folderName)

        assert(result is SaveImageState.Success)
    }
}
