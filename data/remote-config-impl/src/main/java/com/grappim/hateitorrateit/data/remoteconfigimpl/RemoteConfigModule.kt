package com.grappim.hateitorrateit.data.remoteconfigimpl

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.grappim.hateitorrateit.commons.AppInfoData
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
        appInfoData: AppInfoData,
        @ApplicationContext context: Context
    ): FirebaseRemoteConfig {
        FirebaseApp.initializeApp(context)
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val minimumFetchInterval: Long = if (appInfoData.isDebug) {
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
