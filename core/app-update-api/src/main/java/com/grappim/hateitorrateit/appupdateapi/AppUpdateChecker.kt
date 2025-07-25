package com.grappim.hateitorrateit.appupdateapi

import kotlinx.coroutines.flow.Flow

interface AppUpdateChecker {
    fun completeUpdate()

    val updateState: Flow<UpdateState>
    fun checkAndRequestUpdate()
    fun registerUpdateListener()
    fun unregisterUpdateListener()

    fun checkUpdateStateOnResume()
}

sealed class UpdateState {
    data object UpdateDownloaded : UpdateState()
}
