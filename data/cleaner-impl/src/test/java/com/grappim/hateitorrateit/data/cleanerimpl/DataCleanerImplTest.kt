package com.grappim.hateitorrateit.data.cleanerimpl

import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.db.dao.DatabaseDao
import com.grappim.hateitorrateit.data.db.utils.TransactionController
import com.grappim.hateitorrateit.data.db.wrapper.DatabaseWrapper
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.domain.DraftProduct
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.domain.ProductImageData
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
import java.time.OffsetDateTime
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
        transactionController = transactionController,
        databaseDao = databaseDao,
        databaseWrapper = databaseWrapper,
        ioDispatcher = UnconfinedTestDispatcher()
    )

    @Test
    fun `on clearProductImage, with deletable uri, should delete file and remove image from DB`() =
        runTest {
            every { fileUtils.deleteFile(uriString = any()) } returns true
            coEvery { productsRepository.deleteProductImage(any(), any()) } returns Unit

            val actual = dataCleaner.clearProductImage(
                id = 1L,
                imageName = "image",
                uriString = "uri"
            )

            assertTrue(actual)
            verify { fileUtils.deleteFile("uri") }
            coVerify { productsRepository.deleteProductImage(1L, "image") }
        }

    @Test
    fun `on clearProductImage, with non-deletable uri, should not delete file and not remove image from DB`() =
        runTest {
            every { fileUtils.deleteFile(uriString = any()) } returns false
            coEvery { productsRepository.deleteProductImage(any(), any()) } returns Unit

            val actual = dataCleaner.clearProductImage(
                id = 1L,
                imageName = "image",
                uriString = "uri"
            )

            assertFalse(actual)
            verify { fileUtils.deleteFile("uri") }
            coVerify(exactly = 0) { productsRepository.deleteProductImage(1L, "image") }
        }

    @Test
    fun `on deleteProductFileData, should call clearProductImage for each item in list`() =
        runTest {
            every { fileUtils.deleteFile(uriString = any()) } returns true
            coEvery { productsRepository.deleteProductImage(any(), any()) } returns Unit

            dataCleaner.deleteProductFileData(
                id = 1L,
                list = listOf(
                    ProductImageData(
                        name = "Olive Gordon",
                        mimeType = "dicit",
                        uriPath = "brute",
                        uriString = "luctus",
                        size = 9221,
                        md5 = "nullam"
                    ),
                    ProductImageData(
                        name = "Olive Gordon2",
                        mimeType = "dicit4",
                        uriPath = "brute1",
                        uriString = "luctus3",
                        size = 123,
                        md5 = "nullcam"
                    ),
                    ProductImageData(
                        name = "Olive Gordon1",
                        mimeType = "dicit5",
                        uriPath = "brute2",
                        uriString = "luctus5",
                        size = 9221,
                        md5 = "nullam1"
                    ),
                )
            )

            verify(exactly = 3) { fileUtils.deleteFile(uriString = any()) }
            coVerify(exactly = 3) { productsRepository.deleteProductImage(any(), any()) }
        }

    @Test
    fun `on clearProductData with id, should call deleteFolder and removeProductById`() =
        runTest {
            every { fileUtils.deleteFolder(any()) } returns Unit
            coEvery { productsRepository.removeProductById(any()) } returns Unit

            dataCleaner.clearProductData(
                id = 1L,
                productFolderName = "folder"
            )

            verify { fileUtils.deleteFolder("folder") }
            coVerify { productsRepository.removeProductById(1L) }
        }

    @Test
    fun `on clearProductData with draftProduct, should call deleteFolder and removeProductById`() =
        runTest {
            every { fileUtils.deleteFolder(any()) } returns Unit
            coEvery { productsRepository.removeProductById(any()) } returns Unit

            dataCleaner.clearProductData(
                draftProduct = DraftProduct(
                    id = 1L,
                    date = OffsetDateTime.now(),
                    folderName = "folder",
                    type = HateRateType.HATE,
                )
            )

            verify { fileUtils.deleteFolder("folder") }
            coVerify { productsRepository.removeProductById(1L) }
        }

    @Test
    fun `on clearAllData, should call the needed functions`() = runTest {
        coEvery { databaseWrapper.clearAllTables() } just Runs
        coEvery { databaseDao.clearPrimaryKeyIndex() } just Runs
        coEvery { transactionController.runInTransaction(any()) } coAnswers  {
            firstArg<suspend () -> Unit>().invoke()
        }
        every { fileUtils.clearMainFolder() } just Runs

        dataCleaner.clearAllData()

        coVerify { databaseWrapper.clearAllTables() }
        coVerify { databaseDao.clearPrimaryKeyIndex() }
        verify { fileUtils.clearMainFolder() }
    }
}
