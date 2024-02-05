package com.grappim.hateitorrateit.utils.productmanager

import com.grappim.hateitorrateit.utils.FileUtils
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

    private val fileUtils: FileUtils = mockk()

    private lateinit var productImageManager: ProductImageManager

    @Before
    fun setup() {
        productImageManager = ProductImageManagerImpl(
            fileUtils = fileUtils,
        )
    }

    @Test
    fun `copyToBackupFolder should call correct methods`() = runTest {
        val source = "source"
        val destination = "source_backup"
        coEvery { fileUtils.copySourceFilesToDestination(any(), any()) } just Runs
        every { fileUtils.getBackupFolderName(any()) } returns destination

        productImageManager.copyToBackupFolder(source)

        coVerify {
            fileUtils.copySourceFilesToDestination(
                sourceFolderName = source,
                destinationFolderName = destination
            )
        }
        verify {
            fileUtils.getBackupFolderName(source)
        }
    }

    @Test
    fun `moveFromTempToOriginalFolder should call correct methods`() = runTest {
        val source = "folderName_temp"
        val folderName = "folderName"
        coEvery { fileUtils.moveSourceFilesToDestinationFolder(any(), any()) } just Runs
        every { fileUtils.getTempFolderName(any()) } returns source

        productImageManager.moveFromTempToOriginalFolder(folderName)

        coVerify {
            fileUtils.moveSourceFilesToDestinationFolder(
                sourceFolderName = source,
                destinationFolderName = folderName
            )
        }
        verify {
            fileUtils.getTempFolderName(folderName)
        }
    }

    @Test
    fun `moveFromBackupToOriginalFolder should call correct methods`() = runTest {
        val source = "folderName_backup"
        val folderName = "folderName"
        coEvery { fileUtils.moveSourceFilesToDestinationFolder(any(), any()) } just Runs
        every { fileUtils.getBackupFolderName(any()) } returns source

        productImageManager.moveFromBackupToOriginalFolder(folderName)

        coVerify {
            fileUtils.moveSourceFilesToDestinationFolder(
                sourceFolderName = source,
                destinationFolderName = folderName
            )
        }
        verify {
            fileUtils.getBackupFolderName(folderName)
        }
    }
}
