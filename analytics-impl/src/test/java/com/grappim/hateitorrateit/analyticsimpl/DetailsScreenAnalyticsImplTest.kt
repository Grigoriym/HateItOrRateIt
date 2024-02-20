package com.grappim.hateitorrateit.analyticsimpl

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsapi.DetailsScreenAnalytics
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class DetailsScreenAnalyticsImplTest {
    private val analyticsController: AnalyticsController = mockk()

    private lateinit var sut: DetailsScreenAnalytics

    @Before
    fun setup() {
        sut = DetailsScreenAnalyticsImpl(analyticsController)

        every { analyticsController.trackEvent(any()) } just Runs
    }

    @Test
    fun `on trackDetailsScreenStart should track correct event`() {
        sut.trackDetailsScreenStart()

        verify { analyticsController.trackEvent(DETAILS_SCREEN_START) }
    }

    @Test
    fun `on trackDetailsDeleteProductButtonClicked should track correct event`() {
        sut.trackDetailsDeleteProductButtonClicked()

        verify { analyticsController.trackEvent(DETAILS_DELETE_PRODUCT_BUTTON_CLICKED) }
    }

    @Test
    fun `on trackDetailsDeleteProductConfirmed should track correct event`() {
        sut.trackDetailsDeleteProductConfirmed()

        verify { analyticsController.trackEvent(DETAILS_DELETE_PRODUCT_CONFIRMED) }
    }

    @Test
    fun `on trackDetailsEditButtonClicked should track correct event`() {
        sut.trackDetailsEditButtonClicked()

        verify { analyticsController.trackEvent(DETAILS_EDIT_BUTTON_CLICKED) }
    }
}
