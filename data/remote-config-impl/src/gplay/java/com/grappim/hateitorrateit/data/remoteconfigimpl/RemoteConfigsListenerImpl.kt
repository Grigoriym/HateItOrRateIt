package com.grappim.hateitorrateit.data.remoteconfigimpl

import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.configUpdates
import com.google.firebase.remoteconfig.get
import com.grappim.hateitorrateit.core.async.IoDispatcher
import com.grappim.hateitorrateit.data.remoteconfigapi.GITHUB_REPO_URL_KEY
import com.grappim.hateitorrateit.data.remoteconfigapi.IN_APP_UPDATE_ENABLED
import com.grappim.hateitorrateit.data.remoteconfigapi.PRIVACY_POLICY_URL_KEY
import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigsListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class RemoteConfigsListenerImpl @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : RemoteConfigsListener {

    private val scope = CoroutineScope(ioDispatcher)

    private val remoteConfigUpdates: Flow<ConfigUpdate>
        get() = firebaseRemoteConfig.configUpdates

    override val githubRepoLink: StateFlow<String>
        get() = createRemoteConfigStateFlow(
            key = GITHUB_REPO_URL_KEY,
            value = {
                getGithubRepoUrlValue()
            },
            defaultValue = getGithubRepoUrlValue()
        )

    override val privacyPolicy: StateFlow<String>
        get() = createRemoteConfigStateFlow(
            key = PRIVACY_POLICY_URL_KEY,
            value = {
                getPrivacyPolicyUrlValue()
            },
            defaultValue = getPrivacyPolicyUrlValue()
        )
    override val inAppUpdateEnabled: Flow<Boolean>
        get() = createRemoteConfigStateFlow(
            key = IN_APP_UPDATE_ENABLED,
            value = {
                getInAppUpdateValue()
            },
            defaultValue = getInAppUpdateValue()
        )

    override fun onClose() {
        scope.cancel()
    }

    private fun <T> createRemoteConfigStateFlow(
        key: String,
        value: () -> T,
        defaultValue: T
    ): StateFlow<T> {
        return remoteConfigUpdates
            .filter { it.updatedKeys.contains(key) }
            .onEach { firebaseRemoteConfig.activate().await() }
            .map { value() }
            .catch { Timber.e(it) }
            .stateIn(scope, SharingStarted.Lazily, defaultValue)
    }

    private fun getGithubRepoUrlValue() = firebaseRemoteConfig[GITHUB_REPO_URL_KEY].asString()

    private fun getPrivacyPolicyUrlValue() = firebaseRemoteConfig[PRIVACY_POLICY_URL_KEY].asString()

    private fun getInAppUpdateValue() = firebaseRemoteConfig[IN_APP_UPDATE_ENABLED].asBoolean()
}
