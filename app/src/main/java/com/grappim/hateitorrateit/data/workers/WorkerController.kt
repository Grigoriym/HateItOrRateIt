package com.grappim.hateitorrateit.data.workers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerController @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun startCleaning() {
        WorkManager.getInstance(context)
            .beginUniqueWork(
                "cleaning_work",
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<CleanUnusedDataWorker>().build()
            ).enqueue()
    }
}