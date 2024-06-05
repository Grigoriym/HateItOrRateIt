package com.grappim.hateitorrateit.data.analyticsimpl

import com.grappim.hateitorrateit.data.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.data.analyticsapi.HomeAnalytics
import javax.inject.Inject

internal const val HOME_SCREEN_START = "home_screen_start"
internal const val HOME_PRODUCT_CLICKED = "home_product_clicked"

class HomeAnalyticsImpl @Inject constructor(
    private val analyticsController: AnalyticsController
) : HomeAnalytics {
    override fun trackHomeScreenStart() {
        analyticsController.trackEvent(HOME_SCREEN_START)
    }

    override fun trackProductClicked() {
        analyticsController.trackEvent(HOME_PRODUCT_CLICKED)
    }
}
