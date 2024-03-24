package com.grappim.hateitorrateit.data.remoteconfigapi

import kotlinx.coroutines.flow.Flow

interface RemoteConfigsListener {
    val githubRepoLink: Flow<String>
    val privacyPolicy: Flow<String>
    val inAppUpdateEnabled: Flow<Boolean>
    fun onClose()
}
