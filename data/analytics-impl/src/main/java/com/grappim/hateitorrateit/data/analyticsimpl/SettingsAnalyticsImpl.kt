package com.grappim.hateitorrateit.data.analyticsimpl

import com.grappim.hateitorrateit.data.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.data.analyticsapi.SettingsAnalytics
import javax.inject.Inject

internal const val SETTINGS_SCREEN_START = "settings_screen_start"
internal const val ALL_DATA_CLEAR_CONFIRM = "all_data_clear_confirm"
internal const val DEFAULT_TYPE_CHANGED_TO = "default_type_changed_to"
internal const val TYPE = "type"

class SettingsAnalyticsImpl @Inject constructor(
    private val analyticsController: AnalyticsController
) : SettingsAnalytics {
    override fun trackSettingsScreenStart() {
        analyticsController.trackEvent(SETTINGS_SCREEN_START)
    }

    override fun trackAllDataClearedConfirm() {
        analyticsController.trackEvent(ALL_DATA_CLEAR_CONFIRM)
    }

    override fun trackDefaultTypeChangedTo(type: String) {
        analyticsController.trackEvent(DEFAULT_TYPE_CHANGED_TO, mapOf(TYPE to type))
    }
}
