package com.grappim.hateitorrateit

import android.app.Application
import android.os.StrictMode
import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class HateItOrRateItApplication : Application() {

    @Inject
    lateinit var remoteConfigManager: RemoteConfigManager

    private val applicationScope = MainScope()

    /**
     * I will leave it here on how to access something from DI
     * before onCreate(). In Hilt the initialization is done after onCreate()
     */
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AppInfoProviderFactory {
        fun getAppInfoProvider(): AppInfoProvider
    }

    override fun onCreate() {
        setupStrictMode()
        super.onCreate()
        applicationScope.launch {
            remoteConfigManager.fetchRemoteConfig()
        }
    }

    private fun setupStrictMode() {
        val factory = EntryPointAccessors.fromApplication(
            context = this,
            entryPoint = AppInfoProviderFactory::class.java
        )
        val appInfoProvider = factory.getAppInfoProvider()

        if (appInfoProvider.isDebug()) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }
}
