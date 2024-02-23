@file:Suppress("TooGenericExceptionCaught")

package com.grappim.hateitorrateit.data.workerimpl

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.CancellationException

@HiltWorker
class CleanUnusedDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val productsRepository: ProductsRepository,
    private val dataCleaner: DataCleaner
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        try {
            val emptyFiles = productsRepository.getEmptyFiles()
            emptyFiles.forEach { file ->
                Timber.d("Cleaning unused data: $file")
                dataCleaner.clearProductData(
                    productId = file.id,
                    productFolderName = file.productFolderName
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e)
            return Result.failure()
        }
        return Result.success()
    }
}
