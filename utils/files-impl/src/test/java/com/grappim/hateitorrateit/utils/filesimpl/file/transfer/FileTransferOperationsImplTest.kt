package com.grappim.hateitorrateit.utils.filesimpl.file.transfer

import com.grappim.hateitorrateit.testing.domain.getRandomString
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import com.grappim.hateitorrateit.utils.filesapi.transfer.FileTransferOperations
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE
)
class FileTransferOperationsImplTest {

    private lateinit var sut: FileTransferOperations

    private val folderPathManager: FolderPathManager = mockk()

    private val context = RuntimeEnvironment.getApplication()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        sut = FileTransferOperationsImpl(
            ioDispatcher = UnconfinedTestDispatcher(),
            folderPathManager = folderPathManager
        )
    }

    @Test
    fun `on moveSourceFilesToDestinationFolder should correctly move files from source to destination and delete source folder`() =
        runTest {
            val sourceFolder = File(context.filesDir, "products/source")
            assertTrue(sourceFolder.mkdirs())

            val file1 = File(sourceFolder, "testFile1.jpg").apply { createNewFile() }
            val file2 = File(sourceFolder, "testFile2.jpg").apply { createNewFile() }

            val destinationFolder = File(context.filesDir, "products/destination")
            assertTrue(destinationFolder.mkdirs())

            every { folderPathManager.getMainFolder(any()) } returns File(
                context.filesDir,
                "products/source"
            ) andThen File(context.filesDir, "products/destination") andThen File(
                context.filesDir,
                "products/source"
            )

            sut.moveSourceFilesToDestinationFolder(
                sourceFolder.name,
                destinationFolder.name
            )

            val destinationFiles = destinationFolder.list() ?: emptyArray()
            assertTrue(destinationFiles.isNotEmpty())
            assertTrue(destinationFiles.contains(file1.name))
            assertTrue(destinationFiles.contains(file2.name))

            assertFalse(sourceFolder.exists())
            assertFalse(file1.exists())
            assertFalse(file2.exists())
        }

    @Test
    fun `on copySourceFilesToDestination should copy source folder contents to destination folder`() =
        runTest {
            every { folderPathManager.getMainFolder(any()) } returns File(
                context.filesDir,
                "products"
            )

            val sourceFolder = File(context.filesDir, "products/source")
            assertTrue(sourceFolder.mkdirs())

            val file1 = File(sourceFolder, "testFile1.jpg").apply { createNewFile() }
            val file2 = File(sourceFolder, "testFile2.jpg").apply { createNewFile() }

            val destinationFolder = File(context.filesDir, "products/destination")
            assertTrue(destinationFolder.mkdirs())

            every { folderPathManager.getMainFolder(any()) } returns File(
                context.filesDir,
                "products/source"
            ) andThen File(context.filesDir, "products/destination")

            sut.copySourceFilesToDestination(
                sourceFolder.name,
                destinationFolder.name
            )

            val destinationFiles = destinationFolder.list() ?: emptyArray()
            assertTrue(destinationFiles.isNotEmpty())
            assertTrue(destinationFiles.contains(file1.name))
            assertTrue(destinationFiles.contains(file2.name))

            assertTrue(sourceFolder.exists())
            assertTrue(file1.exists())
            assertTrue(file2.exists())

            verify { folderPathManager.getMainFolder(sourceFolder.name) }
        }

    @Test
    fun `on writeSourceFileToTargetFile copies file successfully`() = runTest {
        val content = getRandomString().toByteArray()

        val sourceFolder = File(context.filesDir, "products/source")
        assertTrue(sourceFolder.mkdirs())

        val sourceFile = File(sourceFolder, "testFile1.jpg").apply {
            createNewFile()
            writeBytes(content)
        }
        val targetFile = File(sourceFolder, "testFile2.jpg").apply { createNewFile() }

        sut.writeSourceFileToTargetFile(sourceFile, targetFile)
        assertTrue(targetFile.exists())
        assertTrue(targetFile.readBytes().contentEquals(content))
    }
}
