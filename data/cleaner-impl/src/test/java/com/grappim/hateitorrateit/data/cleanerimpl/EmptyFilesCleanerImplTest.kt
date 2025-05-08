package com.grappim.hateitorrateit.data.cleanerimpl

import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.EmptyFile
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class EmptyFilesCleanerImplTest {
    private val productsRepository: ProductsRepository = mockk()
    private val dataCleaner: DataCleaner = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val sut =
        EmptyFilesCleanerImpl(
            dispatcher = UnconfinedTestDispatcher(),
            dataCleaner = dataCleaner,
            productsRepository = productsRepository
        )

    private val emptyFiles = listOf(
        EmptyFile(id = 7756, productFolderName = "Kathy Copeland"),
        EmptyFile(id = 7757, productFolderName = "Ka123thy Copeland"),
        EmptyFile(id = 77526, productFolderName = "Ka44thy Copeland")
    )

    @Test
    fun `on clean with no error should just run without errors`() = runTest {
        coEvery { productsRepository.getEmptyFiles() } returns emptyFiles
        emptyFiles.forEach { _ ->
            coEvery { dataCleaner.deleteProductData(any(), any()) } just Runs
        }

        sut.clean()

        coVerify { productsRepository.getEmptyFiles() }
        emptyFiles.forEach { emptyFile ->
            coVerify {
                dataCleaner.deleteProductData(
                    productId = emptyFile.id,
                    productFolderName = emptyFile.productFolderName
                )
            }
        }
    }

    @Test
    fun `on clean with error with repo should not run the dataCleaner`() = runTest {
        coEvery { productsRepository.getEmptyFiles() } throws IllegalStateException("error")

        sut.clean()

        coVerify { productsRepository.getEmptyFiles() }
        emptyFiles.forEach { emptyFile ->
            coVerify(exactly = 0) {
                dataCleaner.deleteProductData(
                    productId = emptyFile.id,
                    productFolderName = emptyFile.productFolderName
                )
            }
        }
    }
}
