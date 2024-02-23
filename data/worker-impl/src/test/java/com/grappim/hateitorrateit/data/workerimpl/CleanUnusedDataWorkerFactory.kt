package com.grappim.hateitorrateit.data.workerimpl

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository

class CleanUnusedDataWorkerFactory(
    private val repo: ProductsRepository,
    private val dataCleaner: DataCleaner
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return CleanUnusedDataWorker(appContext, workerParameters, repo, dataCleaner)
    }
}
