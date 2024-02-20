package com.grappim.hateitorrateit.analyticsimpl

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsapi.ProductManagerAnalytics
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ProductManagerAnalyticsImplTest {

    private lateinit var sut: ProductManagerAnalytics

    private val analyticsController: AnalyticsController = mockk()

    @Before
    fun setup() {
        sut = ProductManagerAnalyticsImpl(analyticsController)

        every { analyticsController.trackEvent(any()) } just Runs
    }

    @Test
    fun `on trackCameraButtonClicked should track correct event`() {
        sut.trackCameraButtonClicked()

        verify { analyticsController.trackEvent(CAMERA_BUTTON_CLICKED) }
    }

    @Test
    fun `on trackGalleryButtonClicked should track correct event`() {
        sut.trackGalleryButtonClicked()

        verify { analyticsController.trackEvent(GALLERY_BUTTON_CLICKED) }
    }

    @Test
    fun `on trackDeleteImageClicked should track correct event`() {
        sut.trackDeleteImageClicked()

        verify { analyticsController.trackEvent(DELETE_IMAGE_CLICKED) }
    }

    @Test
    fun `on trackSaveButtonClicked should track correct event`() {
        sut.trackSaveButtonClicked()

        verify { analyticsController.trackEvent(SAVE_BUTTON_CLICKED) }
    }

    @Test
    fun `on trackCreateButtonClicked should track correct event`() {
        sut.trackCreateButtonClicked()

        verify { analyticsController.trackEvent(CREATE_BUTTON_CLICKED) }
    }

    @Test
    fun `on trackProductManagerNewProductStart should track correct event`() {
        sut.trackProductManagerNewProductStart()

        verify { analyticsController.trackEvent(PRODUCT_MANAGER_NEW_PRODUCT_START) }
    }

    @Test
    fun `on trackProductManagerProductToEditStart should track correct event`() {
        sut.trackProductManagerProductToEditStart()

        verify { analyticsController.trackEvent(PRODUCT_MANAGER_PRODUCT_TO_EDIT_START) }
    }
}
