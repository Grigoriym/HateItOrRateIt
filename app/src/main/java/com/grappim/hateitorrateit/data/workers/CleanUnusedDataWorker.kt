package com.grappim.hateitorrateit.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.grappim.hateitorrateit.core.DataCleaner
import com.grappim.hateitorrateit.data.DocsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class CleanUnusedDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val docsRepository: DocsRepository,
    private val dataCleaner: DataCleaner,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val emptyFiles = docsRepository.getEmptyFiles()
        emptyFiles.forEach { file ->
            Timber.d("Cleaning unused data: $file")
            dataCleaner.clearDocumentData(
                file.documentEntity.documentId,
                file.documentEntity.documentFolderName,
            )
        }
        return Result.success()
    }
}
