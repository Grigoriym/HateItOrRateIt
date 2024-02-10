package com.grappim.hateitorrateit.analyticsimpl

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsapi.SettingsScreenAnalytics
import com.grappim.hateitorrateit.domain.HateRateType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsScreenAnalyticsImpl @Inject constructor(
    private val analyticsController: AnalyticsController
) : SettingsScreenAnalytics {
    override fun trackSettingsScreenStart() {
        analyticsController.trackEvent("settings_screen_start")
    }

    override fun trackAllDataClearedConfirm() {
        analyticsController.trackEvent("all_data_clear_confirm")
    }

    override fun trackDefaultTypeChangedTo(type: HateRateType) {
        analyticsController.trackEvent("default_type_changed_to", mapOf("type" to type.name))
    }
}
