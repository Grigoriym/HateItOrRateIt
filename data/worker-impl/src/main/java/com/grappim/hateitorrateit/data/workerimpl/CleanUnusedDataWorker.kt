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

@HiltWorker
class CleanUnusedDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val productsRepository: ProductsRepository,
    private val dataCleaner: DataCleaner,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val emptyFiles = productsRepository.getEmptyFiles()
        emptyFiles.forEach { file ->
            Timber.d("Cleaning unused data: $file")
            dataCleaner.clearProductData(
                productId = file.id,
                productFolderName = file.productFolderName,
            )
        }
        return Result.success()
    }
}
