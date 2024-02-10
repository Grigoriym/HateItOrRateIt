package com.grappim.hateitorrateit.analyticsimpl

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsapi.ProductManagerAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductManagerAnalyticsImpl @Inject constructor(
    private val analyticsController: AnalyticsController
) : ProductManagerAnalytics {
    override fun trackCameraButtonClicked() {
        analyticsController.trackEvent("camera_button_clicked")
    }

    override fun trackGalleryButtonClicked() {
        analyticsController.trackEvent("gallery_button_clicked")
    }

    override fun trackDeleteImageClicked() {
        analyticsController.trackEvent("delete_image_clicked")
    }

    override fun trackSaveButtonClicked() {
        analyticsController.trackEvent("save_button_clicked")
    }

    override fun trackCreateButtonClicked() {
        analyticsController.trackEvent("create_button_clicked")
    }

    override fun trackProductManagerNewProductStart() {
        analyticsController.trackEvent("product_manager_new_product_start")
    }

    override fun trackProductManagerProductToEditStart() {
        analyticsController.trackEvent("product_manager_product_to_edit_start")
    }
}
