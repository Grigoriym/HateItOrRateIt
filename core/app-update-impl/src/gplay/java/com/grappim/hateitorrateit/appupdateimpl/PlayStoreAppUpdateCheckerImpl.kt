package com.grappim.hateitorrateit.appupdateimpl

import android.app.Activity
import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.grappim.hateitorrateit.appupdateapi.AppUpdateChecker
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import timber.log.Timber
import javax.inject.Inject

@ActivityScoped
class PlayStoreAppUpdateCheckerImpl @Inject constructor(
    @ActivityContext private val context: Context
) : AppUpdateChecker {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)

    override fun checkForUpdates(onUpdateDownloaded: () -> Unit) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            Timber.d("appUpdateInfo: $info")
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            if (info.isFlexibleUpdateAllowed && isUpdateAvailable) {
                val type = AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    context as Activity,
                    type,
                    0
                )
            }
            if (info.installStatus() == InstallStatus.DOWNLOADED) {
                onUpdateDownloaded()
            }
        }.addOnFailureListener {
            Timber.e(it, "appUpdateInfo failure")
        }
    }

    override fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }
}
