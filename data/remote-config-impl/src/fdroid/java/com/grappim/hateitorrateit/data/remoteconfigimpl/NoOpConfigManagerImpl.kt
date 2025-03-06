package com.grappim.hateitorrateit.data.remoteconfigimpl

import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigManager
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoOpConfigManagerImpl @Inject constructor() : RemoteConfigManager {
    override suspend fun fetchRemoteConfig() {
        Timber.d("fetchRemoteConfig")
    }
}
