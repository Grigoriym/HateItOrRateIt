package com.grappim.hateitorrateit.analyticsimpl

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsapi.HomeAnalytics
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class HomeAnalyticsImplTest {
    private lateinit var sut: HomeAnalytics

    private val analyticsController: AnalyticsController = mockk()

    @Before
    fun setup() {
        sut = HomeAnalyticsImpl(analyticsController)

        every { analyticsController.trackEvent(any()) } just Runs
    }

    @Test
    fun `on trackHomeScreenStart should track correct event`() {
        sut.trackHomeScreenStart()

        verify { analyticsController.trackEvent(HOME_SCREEN_START) }
    }

    @Test
    fun `on trackProductClicked should track correct event`() {
        sut.trackProductClicked()

        verify { analyticsController.trackEvent(HOME_PRODUCT_CLICKED) }
    }
}
