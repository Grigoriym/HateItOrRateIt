package com.grappim.hateitorrateit.analyticsimpl

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsapi.SettingsScreenAnalytics
import com.grappim.hateitorrateit.domain.HateRateType
import javax.inject.Inject
import javax.inject.Singleton

internal const val SETTINGS_SCREEN_START = "settings_screen_start"
internal const val ALL_DATA_CLEAR_CONFIRM = "all_data_clear_confirm"
internal const val DEFAULT_TYPE_CHANGED_TO = "default_type_changed_to"
internal const val TYPE = "type"

@Singleton
class SettingsScreenAnalyticsImpl @Inject constructor(
    private val analyticsController: AnalyticsController
) : SettingsScreenAnalytics {
    override fun trackSettingsScreenStart() {
        analyticsController.trackEvent(SETTINGS_SCREEN_START)
    }

    override fun trackAllDataClearedConfirm() {
        analyticsController.trackEvent(ALL_DATA_CLEAR_CONFIRM)
    }

    override fun trackDefaultTypeChangedTo(type: HateRateType) {
        analyticsController.trackEvent(DEFAULT_TYPE_CHANGED_TO, mapOf(TYPE to type.name))
    }
}
