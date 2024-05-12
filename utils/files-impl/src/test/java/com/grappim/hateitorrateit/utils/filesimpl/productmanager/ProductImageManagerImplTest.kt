package com.grappim.hateitorrateit.utils.filesimpl.productmanager

import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import com.grappim.hateitorrateit.utils.filesapi.productmanager.ProductImageManager
import com.grappim.hateitorrateit.utils.filesapi.transfer.FileTransferOperations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ProductImageManagerImplTest {

    private val folderPathManager: FolderPathManager = mockk()
    private val fileTransferOperations: FileTransferOperations = mockk()

    private lateinit var productImageManager: ProductImageManager

    @Before
    fun setup() {
        productImageManager = ProductImageManagerImpl(
            folderPathManager = folderPathManager,
            fileTransferOperations = fileTransferOperations
        )
    }

    @Test
    fun `copyToBackupFolder should call correct methods`() = runTest {
        val source = "source"
        val destination = "source_backup"
        coEvery { fileTransferOperations.copySourceFilesToDestination(any(), any()) } just Runs
        every { folderPathManager.getBackupFolderName(any()) } returns destination

        productImageManager.copyToBackupFolder(source)

        coVerify {
            fileTransferOperations.copySourceFilesToDestination(
                sourceFolderName = source,
                destinationFolderName = destination
            )
        }
        verify {
            folderPathManager.getBackupFolderName(source)
        }
    }

    @Test
    fun `moveFromTempToOriginalFolder should call correct methods`() = runTest {
        val source = "folderName_temp"
        val folderName = "folderName"
        coEvery {
            fileTransferOperations.moveSourceFilesToDestinationFolder(
                any(),
                any()
            )
        } just Runs
        every { folderPathManager.getTempFolderName(any()) } returns source

        productImageManager.moveFromTempToOriginalFolder(folderName)

        coVerify {
            fileTransferOperations.moveSourceFilesToDestinationFolder(
                sourceFolderName = source,
                destinationFolderName = folderName
            )
        }
        verify {
            folderPathManager.getTempFolderName(folderName)
        }
    }

    @Test
    fun `moveFromBackupToOriginalFolder should call correct methods`() = runTest {
        val source = "folderName_backup"
        val folderName = "folderName"
        coEvery {
            fileTransferOperations.moveSourceFilesToDestinationFolder(
                any(),
                any()
            )
        } just Runs
        every { folderPathManager.getBackupFolderName(any()) } returns source

        productImageManager.moveFromBackupToOriginalFolder(folderName)

        coVerify {
            fileTransferOperations.moveSourceFilesToDestinationFolder(
                sourceFolderName = source,
                destinationFolderName = folderName
            )
        }
        verify {
            folderPathManager.getBackupFolderName(folderName)
        }
    }
}
