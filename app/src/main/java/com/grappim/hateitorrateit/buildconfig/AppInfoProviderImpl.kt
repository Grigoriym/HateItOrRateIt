package com.grappim.hateitorrateit.buildconfig

import com.grappim.hateitorrateit.BuildConfig
import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import javax.inject.Inject

class AppInfoProviderImpl @Inject constructor() : AppInfoProvider {
    override fun getAppInfo(): String = "${BuildConfig.VERSION_NAME} - " +
        "${BuildConfig.VERSION_CODE} - " +
        BuildConfig.BUILD_TYPE

    override fun isDebug(): Boolean = BuildConfig.DEBUG
}
