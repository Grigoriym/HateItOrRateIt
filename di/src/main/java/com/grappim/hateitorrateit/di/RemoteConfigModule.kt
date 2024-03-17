package com.grappim.hateitorrateit.di

import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigManager
import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigsListener
import com.grappim.hateitorrateit.data.remoteconfigimpl.RemoteConfigManagerImpl
import com.grappim.hateitorrateit.data.remoteconfigimpl.RemoteConfigsListenerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface RemoteConfigModule {
    @Binds
    fun bindRemoteConfigManager(
        remoteConfigManagerImpl: RemoteConfigManagerImpl
    ): RemoteConfigManager

    @Binds
    fun bindRemoteConfigListener(
        remoteConfigsListenerImpl: RemoteConfigsListenerImpl
    ): RemoteConfigsListener
}
