package com.grappim.hateitorrateit.data.remoteconfigapi

interface RemoteConfigManager {
    suspend fun fetchRemoteConfig()
}
