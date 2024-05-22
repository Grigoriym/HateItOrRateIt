package com.grappim.hateitorrateit.analyticsapi

import com.grappim.hateitorrateit.data.repoapi.models.HateRateType

interface SettingsAnalytics {
    fun trackSettingsScreenStart()

    fun trackAllDataClearedConfirm()

    fun trackDefaultTypeChangedTo(type: HateRateType)
}
