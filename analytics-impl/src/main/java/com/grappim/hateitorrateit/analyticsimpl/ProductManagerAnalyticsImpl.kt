package com.grappim.hateitorrateit.analyticsimpl

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsapi.ProductManagerAnalytics
import javax.inject.Inject
import javax.inject.Singleton

internal const val CAMERA_BUTTON_CLICKED = "camera_button_clicked"
internal const val GALLERY_BUTTON_CLICKED = "gallery_button_clicked"
internal const val DELETE_IMAGE_CLICKED = "delete_image_clicked"
internal const val SAVE_BUTTON_CLICKED = "save_button_clicked"
internal const val CREATE_BUTTON_CLICKED = "create_button_clicked"
internal const val PRODUCT_MANAGER_NEW_PRODUCT_START = "product_manager_new_product_start"
internal const val PRODUCT_MANAGER_PRODUCT_TO_EDIT_START = "product_manager_product_to_edit_start"

@Singleton
class ProductManagerAnalyticsImpl @Inject constructor(
    private val analyticsController: AnalyticsController
) : ProductManagerAnalytics {
    override fun trackCameraButtonClicked() {
        analyticsController.trackEvent(CAMERA_BUTTON_CLICKED)
    }

    override fun trackGalleryButtonClicked() {
        analyticsController.trackEvent(GALLERY_BUTTON_CLICKED)
    }

    override fun trackDeleteImageClicked() {
        analyticsController.trackEvent(DELETE_IMAGE_CLICKED)
    }

    override fun trackSaveButtonClicked() {
        analyticsController.trackEvent(SAVE_BUTTON_CLICKED)
    }

    override fun trackCreateButtonClicked() {
        analyticsController.trackEvent(CREATE_BUTTON_CLICKED)
    }

    override fun trackProductManagerNewProductStart() {
        analyticsController.trackEvent(PRODUCT_MANAGER_NEW_PRODUCT_START)
    }

    override fun trackProductManagerProductToEditStart() {
        analyticsController.trackEvent(PRODUCT_MANAGER_PRODUCT_TO_EDIT_START)
    }
}
