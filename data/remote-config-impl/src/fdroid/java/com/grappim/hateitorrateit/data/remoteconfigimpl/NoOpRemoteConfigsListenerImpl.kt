package com.grappim.hateitorrateit.data.remoteconfigimpl

import com.grappim.hateitorrateit.data.remoteconfigapi.FDROID_PRIVACY_POLICY_URL
import com.grappim.hateitorrateit.data.remoteconfigapi.GITHUB_REPO_URL
import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigsListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoOpRemoteConfigsListenerImpl @Inject constructor() : RemoteConfigsListener {
    override val githubRepoLink: Flow<String>
        get() = flowOf(GITHUB_REPO_URL)
    override val privacyPolicy: Flow<String>
        get() = flowOf(FDROID_PRIVACY_POLICY_URL)
    override val inAppUpdateEnabled: Flow<Boolean>
        get() = flowOf(false)

    override fun onClose() {
        Timber.d("onClose")
    }
}
