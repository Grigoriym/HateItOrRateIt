package com.grappim.hateitorrateit.utils.filesimpl.file.transfer

import com.grappim.hateitorrateit.testing.core.FakeFolderPathManager
import com.grappim.hateitorrateit.testing.domain.getRandomString
import com.grappim.hateitorrateit.utils.filesapi.transfer.FileTransferOperations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FileTransferOperationsImplTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun createSut(folderPathManager: FakeFolderPathManager): FileTransferOperations =
        FileTransferOperationsImpl(
            ioDispatcher = UnconfinedTestDispatcher(),
            folderPathManager = folderPathManager
        )

    @Test
    fun `on moveSourceFilesToDestinationFolder should correctly move files from source to destination and delete source folder`() =
        runTest {
            val sourceFolder = File(tempFolder.root, "products/source")
            assertTrue(sourceFolder.mkdirs())

            val file1 = File(sourceFolder, "testFile1.jpg").apply { createNewFile() }
            val file2 = File(sourceFolder, "testFile2.jpg").apply { createNewFile() }

            val destinationFolder = File(tempFolder.root, "products/destination")
            assertTrue(destinationFolder.mkdirs())

            val folderPathManager = FakeFolderPathManager(
                mainFolders = listOf(sourceFolder, destinationFolder, sourceFolder)
            )
            val sut = createSut(folderPathManager)

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
            val sourceFolder = File(tempFolder.root, "products/source")
            assertTrue(sourceFolder.mkdirs())

            val file1 = File(sourceFolder, "testFile1.jpg").apply { createNewFile() }
            val file2 = File(sourceFolder, "testFile2.jpg").apply { createNewFile() }

            val destinationFolder = File(tempFolder.root, "products/destination")
            assertTrue(destinationFolder.mkdirs())

            val folderPathManager = FakeFolderPathManager(
                mainFolders = listOf(sourceFolder, destinationFolder)
            )
            val sut = createSut(folderPathManager)

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

            assertEquals(
                listOf(sourceFolder.name, destinationFolder.name),
                folderPathManager.getMainFolderCalls
            )
        }

    @Test
    fun `on writeSourceFileToTargetFile copies file successfully`() = runTest {
        val content = getRandomString().toByteArray()

        val sourceFolder = File(tempFolder.root, "products/source")
        assertTrue(sourceFolder.mkdirs())

        val sourceFile = File(sourceFolder, "testFile1.jpg").apply {
            createNewFile()
            writeBytes(content)
        }
        val targetFile = File(sourceFolder, "testFile2.jpg").apply { createNewFile() }

        val sut = createSut(FakeFolderPathManager())

        sut.writeSourceFileToTargetFile(sourceFile, targetFile)
        assertTrue(targetFile.exists())
        assertTrue(targetFile.readBytes().contentEquals(content))
    }
}
