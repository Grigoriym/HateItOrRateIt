package com.grappim.hateitorrateit.data.analyticsapi

interface SettingsAnalytics {
    fun trackSettingsScreenStart()

    fun trackAllDataClearedConfirm()

    fun trackDefaultTypeChangedTo(type: String)
}
