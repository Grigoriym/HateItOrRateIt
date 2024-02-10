package com.grappim.hateitorrateit.analyticsimpl

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsapi.HomeScreenAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeScreenAnalyticsImpl @Inject constructor(
    private val analyticsController: AnalyticsController
) : HomeScreenAnalytics {
    override fun trackHomeScreenStart() {
        analyticsController.trackEvent("home_screen_start")
    }

    override fun trackProductClicked() {
        analyticsController.trackEvent("home_product_clicked")
    }
}
