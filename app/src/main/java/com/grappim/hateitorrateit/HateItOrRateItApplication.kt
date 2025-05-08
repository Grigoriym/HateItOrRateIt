package com.grappim.hateitorrateit

import android.app.Application
import android.os.StrictMode
import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class HateItOrRateItApplication : Application() {

    @Inject
    lateinit var remoteConfigManager: RemoteConfigManager

    private val applicationScope = MainScope()

    override fun onCreate() {
        setupStrictMode()
        super.onCreate()
        applicationScope.launch {
            remoteConfigManager.fetchRemoteConfig()
        }
    }

    private fun setupStrictMode() {
        if (BuildConfig.DEBUG) {
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
