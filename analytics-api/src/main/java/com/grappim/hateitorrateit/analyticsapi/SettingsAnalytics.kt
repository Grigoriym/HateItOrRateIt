package com.grappim.hateitorrateit.analyticsapi

import com.grappim.hateitorrateit.domain.HateRateType

interface SettingsAnalytics {
    fun trackSettingsScreenStart()

    fun trackAllDataClearedConfirm()

    fun trackDefaultTypeChangedTo(type: HateRateType)
}
