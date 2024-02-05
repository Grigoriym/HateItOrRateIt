package com.grappim.hateitorrateit.data.cleanerimpl

import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.db.dao.DatabaseDao
import com.grappim.hateitorrateit.data.db.utils.TransactionController
import com.grappim.hateitorrateit.data.db.wrapper.DatabaseWrapper
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.testing.getRandomLong
import com.grappim.hateitorrateit.testing.getRandomString
import com.grappim.hateitorrateit.utils.FileUtils
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DataCleanerImplTest {

    private val fileUtils: FileUtils = mockk()
    private val productsRepository: ProductsRepository = mockk()
    private val transactionController: TransactionController = mockk()
    private val databaseDao: DatabaseDao = mockk()
    private val databaseWrapper: DatabaseWrapper = mockk()

    private val dataCleaner: DataCleaner = DataCleanerImpl(
        fileUtils = fileUtils,
        productsRepository = productsRepository,
        databaseDao = databaseDao,
        databaseWrapper = databaseWrapper,
        ioDispatcher = UnconfinedTestDispatcher()
    )

    @Test
    fun `on clearProductImage, with deletable uri, should delete file and remove image from DB`() =
        runTest {
            val imageName = getRandomString()
            val uriString = getRandomString()
            val productId = getRandomLong()

            every { fileUtils.deleteFile(uriString = any()) } returns true
            coEvery { productsRepository.deleteProductImage(any(), any()) } just Runs

            val actual = dataCleaner.clearProductImage(
                productId = productId,
                imageName = imageName,
                uriString = uriString
            )

            assertTrue(actual)
            verify { fileUtils.deleteFile(uriString) }
            coVerify { productsRepository.deleteProductImage(productId, imageName) }
        }

    @Test
    fun `on clearProductImage, with non-deletable uri, should not delete file and not remove image from DB`() =
        runTest {
            val imageName = getRandomString()
            val uriString = getRandomString()
            val productId = getRandomLong()

            every { fileUtils.deleteFile(uriString = any()) } returns false
            coEvery { productsRepository.deleteProductImage(any(), any()) } just Runs

            val actual = dataCleaner.clearProductImage(
                productId = productId,
                imageName = imageName,
                uriString = uriString
            )

            assertFalse(actual)
            verify { fileUtils.deleteFile(uriString) }
            coVerify(exactly = 0) { productsRepository.deleteProductImage(productId, imageName) }
        }

    @Test
    fun `on deleteProductFileData, should call clearProductImage for each item in list`() =
        runTest {
            val productId = getRandomLong()

            every { fileUtils.deleteFile(uriString = any()) } returns true
            coEvery { productsRepository.deleteProductImage(any(), any()) } just Runs

            val list = listOf(
                ProductImageData(
                    name = getRandomString(),
                    mimeType = "dicit",
                    uriPath = "brute",
                    uriString = getRandomString(),
                    size = 9221,
                    md5 = "nullam"
                ),
                ProductImageData(
                    name = getRandomString(),
                    mimeType = "dicit4",
                    uriPath = "brute1",
                    uriString = getRandomString(),
                    size = 123,
                    md5 = "nullcam"
                ),
                ProductImageData(
                    name = getRandomString(),
                    mimeType = "dicit5",
                    uriPath = "brute2",
                    uriString = getRandomString(),
                    size = 9221,
                    md5 = "nullam1"
                ),
            )

            dataCleaner.deleteProductFileData(
                productId = productId,
                list = list
            )

            list.forEach {
                verify { fileUtils.deleteFile(uriString = it.uriString) }
                coVerify {
                    productsRepository.deleteProductImage(
                        productId = productId,
                        imageName = it.name
                    )
                }
            }
        }

    @Test
    fun `on clearProductData, should call deleteFolder and removeProductById`() =
        runTest {
            val productId = getRandomLong()
            val folderName = getRandomString()

            coEvery { fileUtils.deleteFolder(any()) } just Runs
            coEvery { productsRepository.deleteProductById(any()) } just Runs

            dataCleaner.clearProductData(
                productId = productId,
                productFolderName = folderName
            )

            coVerify { fileUtils.deleteFolder(folderName) }
            coVerify { productsRepository.deleteProductById(productId) }
        }

    @Test
    fun `on deleteTempFolder should get correct folder name and remove it`() = runTest {
        val folderName = getRandomString()

        coEvery { fileUtils.deleteFolder(any()) } just Runs
        every { fileUtils.getTempFolderName(any()) } returns "${folderName}_temp"

        dataCleaner.deleteTempFolder(folderName)

        verify { fileUtils.getTempFolderName(folderName) }
        coVerify { fileUtils.deleteFolder("${folderName}_temp") }
    }

    @Test
    fun `on deleteBackupFolder should get correct folder name and remove it`() = runTest {
        val folderName = getRandomString()

        coEvery { fileUtils.deleteFolder(any()) } just Runs
        every { fileUtils.getBackupFolderName(any()) } returns "${folderName}_backup"

        dataCleaner.deleteBackupFolder(folderName)

        verify { fileUtils.getBackupFolderName(folderName) }
        coVerify { fileUtils.deleteFolder("${folderName}_backup") }
    }

    @Test
    fun `on clearAllData, should call the needed functions`() = runTest {
        coEvery { databaseWrapper.clearAllTables() } just Runs
        coEvery { databaseDao.clearPrimaryKeyIndex() } just Runs
        coEvery { transactionController.runInTransaction(any()) } coAnswers {
            firstArg<suspend () -> Unit>().invoke()
        }
        coEvery { fileUtils.clearMainFolder() } returns true

        dataCleaner.clearAllData()

        coVerify { databaseWrapper.clearAllTables() }
        coVerify { databaseDao.clearPrimaryKeyIndex() }
        coVerify { fileUtils.clearMainFolder() }
    }
}
