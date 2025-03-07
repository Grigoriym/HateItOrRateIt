package com.grappim.hateitorrateit.core.appinfoapi

interface AppInfoProvider {
    fun getAppInfo(): String
    fun isDebug(): Boolean
    fun isFdroidBuild(): Boolean
}
