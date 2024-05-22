package com.grappim.hateitorrateit.analyticsimpl

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsapi.SettingsAnalytics
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class SettingsAnalyticsImplTest {
    private lateinit var sut: SettingsAnalytics

    private val analyticsController: AnalyticsController = mockk()

    @Before
    fun setup() {
        sut = SettingsAnalyticsImpl(analyticsController)

        every { analyticsController.trackEvent(any()) } just Runs
        every { analyticsController.trackEvent(any(), any()) } just Runs
    }

    @Test
    fun `on trackSettingsScreenStart should track correct event`() {
        sut.trackSettingsScreenStart()

        verify { analyticsController.trackEvent(SETTINGS_SCREEN_START) }
    }

    @Test
    fun `on trackAllDataClearedConfirm should track correct event`() {
        sut.trackAllDataClearedConfirm()

        verify { analyticsController.trackEvent(ALL_DATA_CLEAR_CONFIRM) }
    }

    @Test
    fun `on trackDefaultTypeChangedTo should track correct event`() {
        val type = com.grappim.hateitorrateit.data.repoapi.models.HateRateType.HATE

        sut.trackDefaultTypeChangedTo(type)

        verify {
            analyticsController.trackEvent(
                DEFAULT_TYPE_CHANGED_TO,
                mapOf(TYPE to type.name)
            )
        }
    }
}
