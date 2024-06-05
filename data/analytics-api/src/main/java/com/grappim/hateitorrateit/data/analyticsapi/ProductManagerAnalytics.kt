package com.grappim.hateitorrateit.data.analyticsapi

interface ProductManagerAnalytics {
    fun trackCameraButtonClicked()

    fun trackGalleryButtonClicked()

    fun trackDeleteImageClicked()

    fun trackSaveButtonClicked()

    fun trackCreateButtonClicked()

    fun trackProductManagerNewProductStart()

    fun trackProductManagerProductToEditStart()
}
