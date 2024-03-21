package com.grappim.hateitorrateit.data.remoteconfigimpl

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.grappim.hateitorrateit.commons.IoDispatcher
import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigManagerImpl @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : RemoteConfigManager {
    override suspend fun fetchRemoteConfig(): Unit = withContext(ioDispatcher) {
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Timber.d("Remote Config activated configs")
                } else {
                    Timber.d("Remote Config did not activate configs")
                }
            }
    }
}
