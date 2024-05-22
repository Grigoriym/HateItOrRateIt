package com.grappim.hateitorrateit.data.workerimpl

import android.util.Log
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.EmptyFile
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.assertIs

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CleanUnusedDataWorkerTest {
    private val context = RuntimeEnvironment.getApplication()

    private val productsRepository: ProductsRepository = mockk()
    private val dataCleaner: DataCleaner = mockk()

    private val emptyFiles = listOf(
        EmptyFile(id = 7756, productFolderName = "Kathy Copeland"),
        EmptyFile(id = 7757, productFolderName = "Ka123thy Copeland"),
        EmptyFile(id = 77526, productFolderName = "Ka44thy Copeland")
    )

    @Before
    fun setup() {
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

    @Test
    fun `on doWork with no error should return success`() = runTest {
        coEvery { productsRepository.getEmptyFiles() } returns emptyFiles
        emptyFiles.forEach { _ ->
            coEvery { dataCleaner.deleteProductData(any(), any()) } just Runs
        }

        val worker = TestListenableWorkerBuilder<CleanUnusedDataWorker>(context)
            .setWorkerFactory(
                CleanUnusedDataWorkerFactory(
                    productsRepository,
                    dataCleaner
                )
            )
            .build()

        val actual = worker.doWork()

        coVerify { productsRepository.getEmptyFiles() }
        emptyFiles.forEach { emptyFile ->
            coVerify {
                dataCleaner.deleteProductData(
                    productId = emptyFile.id,
                    productFolderName = emptyFile.productFolderName
                )
            }
        }

        assertIs<ListenableWorker.Result.Success>(actual)
    }

    @Test
    fun `on doWork with error should return success`() = runTest {
        coEvery { productsRepository.getEmptyFiles() } throws IllegalStateException("error")

        val worker = TestListenableWorkerBuilder<CleanUnusedDataWorker>(context)
            .setWorkerFactory(
                CleanUnusedDataWorkerFactory(
                    productsRepository,
                    dataCleaner
                )
            )
            .build()

        val actual = worker.doWork()

        coVerify { productsRepository.getEmptyFiles() }
        emptyFiles.forEach { emptyFile ->
            coVerify(exactly = 0) {
                dataCleaner.deleteProductData(
                    productId = emptyFile.id,
                    productFolderName = emptyFile.productFolderName
                )
            }
        }

        assertIs<ListenableWorker.Result.Failure>(actual)
    }
}
