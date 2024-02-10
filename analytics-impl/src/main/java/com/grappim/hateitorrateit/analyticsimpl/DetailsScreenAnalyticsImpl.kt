package com.grappim.hateitorrateit.analyticsimpl

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsapi.DetailsScreenAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DetailsScreenAnalyticsImpl @Inject constructor(
    private val analyticsController: AnalyticsController
) : DetailsScreenAnalytics {
    override fun trackDetailsScreenStart() {
        analyticsController.trackEvent("details_screen_start")
    }

    override fun trackDetailsDeleteProductButtonClicked() {
        analyticsController.trackEvent("details_delete_product_button_clicked")
    }

    override fun trackDetailsDeleteProductConfirmed() {
        analyticsController.trackEvent("details_delete_product_confirmed")
    }

    override fun trackDetailsEditButtonClicked() {
        analyticsController.trackEvent("details_edit_button_clicked")
    }
}
