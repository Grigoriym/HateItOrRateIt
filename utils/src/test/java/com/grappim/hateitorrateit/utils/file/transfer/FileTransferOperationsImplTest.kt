package com.grappim.hateitorrateit.utils.file.transfer

import com.grappim.hateitorrateit.utils.file.pathmanager.FolderPathManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

    private lateinit var fileTransferOperations: FileTransferOperations

    private val folderPathManager: FolderPathManager = mockk()

    private val context = RuntimeEnvironment.getApplication()

    @Before
    fun setUp() {
        fileTransferOperations = FileTransferOperationsImpl(
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

            fileTransferOperations.moveSourceFilesToDestinationFolder(
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

            fileTransferOperations.copySourceFilesToDestination(
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
}
