package com.grappim.hateitorrateit.data.remoteconfigimpl

import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigManager
import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigsListener
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface NoOpRemoteConfigModule {

    @Binds
    fun bindRemoteConfig(impl: NoOpConfigManagerImpl): RemoteConfigManager

    @Binds
    fun bindNoOpRemoteConfigListener(impl: NoOpRemoteConfigsListenerImpl): RemoteConfigsListener
}
