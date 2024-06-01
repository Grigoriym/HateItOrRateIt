package com.grappim.hateitorrateit.analyticsapi

interface SettingsAnalytics {
    fun trackSettingsScreenStart()

    fun trackAllDataClearedConfirm()

    fun trackDefaultTypeChangedTo(type: String)
}
