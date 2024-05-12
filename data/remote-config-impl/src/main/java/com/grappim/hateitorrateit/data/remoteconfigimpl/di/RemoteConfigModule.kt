package com.grappim.hateitorrateit.data.remoteconfigimpl.di

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigManager
import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigsListener
import com.grappim.hateitorrateit.data.remoteconfigimpl.RemoteConfigDefaults
import com.grappim.hateitorrateit.data.remoteconfigimpl.RemoteConfigManagerImpl
import com.grappim.hateitorrateit.data.remoteconfigimpl.RemoteConfigsListenerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class RemoteConfigModule {

    companion object {
        private const val RELEASE_FETCH_INTERVAL = 3600L
    }

    @[Provides Singleton]
    fun provideRemoteConfigDefaults(): RemoteConfigDefaults = RemoteConfigDefaults()

    @[Provides Singleton]
    fun provideRemoteConfig(
        remoteConfigDefaults: RemoteConfigDefaults,
        appInfoProvider: AppInfoProvider,
        @ApplicationContext context: Context
    ): FirebaseRemoteConfig {
        FirebaseApp.initializeApp(context)
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val minimumFetchInterval: Long = if (appInfoProvider.isDebug()) {
            0
        } else {
            RELEASE_FETCH_INTERVAL
        }
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = minimumFetchInterval
        }
        remoteConfig.apply {
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(remoteConfigDefaults.defaults)
        }
        return remoteConfig
    }
}

@[Module InstallIn(SingletonComponent::class)]
interface RemoteConfigBindsModule {
    @Binds
    fun bindRemoteConfigManager(
        remoteConfigManagerImpl: RemoteConfigManagerImpl
    ): RemoteConfigManager

    @Binds
    fun bindRemoteConfigListener(
        remoteConfigsListenerImpl: RemoteConfigsListenerImpl
    ): RemoteConfigsListener
}
