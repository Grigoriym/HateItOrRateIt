package com.grappim.hateitorrateit.analyticsimpl

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsapi.DetailsAnalytics
import javax.inject.Inject
import javax.inject.Singleton

internal const val DETAILS_SCREEN_START = "details_screen_start"
internal const val DETAILS_DELETE_PRODUCT_BUTTON_CLICKED = "details_delete_product_button_clicked"
internal const val DETAILS_DELETE_PRODUCT_CONFIRMED = "details_delete_product_confirmed"
internal const val DETAILS_EDIT_BUTTON_CLICKED = "details_edit_button_clicked"

@Singleton
class DetailsAnalyticsImpl @Inject constructor(
    private val analyticsController: AnalyticsController
) : DetailsAnalytics {
    override fun trackDetailsScreenStart() {
        analyticsController.trackEvent(DETAILS_SCREEN_START)
    }

    override fun trackDetailsDeleteProductButtonClicked() {
        analyticsController.trackEvent(DETAILS_DELETE_PRODUCT_BUTTON_CLICKED)
    }

    override fun trackDetailsDeleteProductConfirmed() {
        analyticsController.trackEvent(DETAILS_DELETE_PRODUCT_CONFIRMED)
    }

    override fun trackDetailsEditButtonClicked() {
        analyticsController.trackEvent(DETAILS_EDIT_BUTTON_CLICKED)
    }
}
