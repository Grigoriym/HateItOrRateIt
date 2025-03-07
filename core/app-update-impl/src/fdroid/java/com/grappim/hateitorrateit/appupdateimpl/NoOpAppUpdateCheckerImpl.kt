package com.grappim.hateitorrateit.appupdateimpl

import com.grappim.hateitorrateit.appupdateapi.AppUpdateChecker
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoOpAppUpdateCheckerImpl @Inject constructor() : AppUpdateChecker {
    override fun checkForUpdates(onUpdateDownloaded: () -> Unit) {
        Timber.d("checkForUpdates")
    }

    override fun completeUpdate() {
        Timber.d("completeUpdate")
    }
}
