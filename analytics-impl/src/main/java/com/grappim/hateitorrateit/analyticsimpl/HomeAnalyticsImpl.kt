package com.grappim.hateitorrateit.analyticsimpl

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsapi.HomeAnalytics
import javax.inject.Inject
import javax.inject.Singleton

internal const val HOME_SCREEN_START = "home_screen_start"
internal const val HOME_PRODUCT_CLICKED = "home_product_clicked"

@Singleton
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
