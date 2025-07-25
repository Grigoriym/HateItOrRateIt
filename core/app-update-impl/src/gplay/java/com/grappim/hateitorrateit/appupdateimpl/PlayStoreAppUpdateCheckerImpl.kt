package com.grappim.hateitorrateit.appupdateimpl

import android.app.Activity
import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.grappim.hateitorrateit.appupdateapi.AppUpdateChecker
import com.grappim.hateitorrateit.appupdateapi.UpdateState
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@ActivityScoped
class PlayStoreAppUpdateCheckerImpl @Inject constructor(
    @ActivityContext private val context: Context
) : AppUpdateChecker {

    private val appUpdateManager: AppUpdateManager by lazy {
        AppUpdateManagerFactory.create(context)
    }
    private val updateType = AppUpdateType.FLEXIBLE

    private val _updateState = MutableSharedFlow<UpdateState>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val updateState: Flow<UpdateState> = _updateState.asSharedFlow()

    override fun checkAndRequestUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.installStatus == InstallStatus.DOWNLOADED) {
                _updateState.tryEmit(UpdateState.UpdateDownloaded)
                return@addOnSuccessListener
            }
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                else -> false
            }
            if (isUpdateAvailable && isUpdateAllowed) {
                val type = AppUpdateOptions.newBuilder(updateType).build()
                appUpdateManager.startUpdateFlow(
                    info,
                    context as Activity,
                    type
                )
            }
        }
    }

    override fun checkUpdateStateOnResume() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.installStatus == InstallStatus.DOWNLOADED) {
                _updateState.tryEmit(UpdateState.UpdateDownloaded)
            }
        }
    }

    override fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }

    override fun registerUpdateListener() {
        appUpdateManager.registerListener(installStateUpdatedListener)
    }

    override fun unregisterUpdateListener() {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus == InstallStatus.DOWNLOADED) {
            _updateState.tryEmit(UpdateState.UpdateDownloaded)
        }
    }
}
