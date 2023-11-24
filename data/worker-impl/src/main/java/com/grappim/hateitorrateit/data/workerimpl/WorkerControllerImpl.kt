package com.grappim.hateitorrateit.data.workerimpl

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.grappim.hateitorrateit.data.workerapi.WorkerController
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : WorkerController {

    override fun startCleaning() {
        WorkManager.getInstance(context)
            .beginUniqueWork(
                "cleaning_work",
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<CleanUnusedDataWorker>().build()
            ).enqueue()
    }
}
