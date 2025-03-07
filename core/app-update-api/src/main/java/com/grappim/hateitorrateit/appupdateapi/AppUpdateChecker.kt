package com.grappim.hateitorrateit.appupdateapi

interface AppUpdateChecker {
    fun checkForUpdates(onUpdateDownloaded: () -> Unit)
    fun completeUpdate()
}
