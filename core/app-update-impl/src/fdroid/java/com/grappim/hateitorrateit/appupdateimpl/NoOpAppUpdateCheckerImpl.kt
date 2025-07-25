package com.grappim.hateitorrateit.appupdateimpl

import com.grappim.hateitorrateit.appupdateapi.AppUpdateChecker
import com.grappim.hateitorrateit.appupdateapi.UpdateState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoOpAppUpdateCheckerImpl @Inject constructor() : AppUpdateChecker {

    override fun completeUpdate() {
        Timber.d("completeUpdate")
    }

    override val updateState: Flow<UpdateState>
        get() = flowOf()

    override fun checkAndRequestUpdate() {
        Timber.d("checkAndRequestUpdate")
    }

    override fun registerUpdateListener() {
        Timber.d("registerUpdateListener")
    }

    override fun unregisterUpdateListener() {
        Timber.d("unregisterUpdateListener")
    }

    override fun checkUpdateStateOnResume() {
        Timber.d("checkUpdateStateOnResume")
    }
}
