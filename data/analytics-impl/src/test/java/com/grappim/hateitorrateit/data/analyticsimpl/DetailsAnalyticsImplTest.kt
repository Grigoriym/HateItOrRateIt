package com.grappim.hateitorrateit.data.analyticsimpl

import com.grappim.hateitorrateit.data.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.data.analyticsapi.DetailsAnalytics
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class DetailsAnalyticsImplTest {
    private val analyticsController: AnalyticsController = mockk()

    private lateinit var sut: DetailsAnalytics

    @Before
    fun setup() {
        sut = DetailsAnalyticsImpl(analyticsController)

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
