package com.grappim.hateitorrateit.feature.settings.ui.screen

import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import com.grappim.hateitorrateit.data.analyticsapi.SettingsAnalytics
import com.grappim.hateitorrateit.testing.core.MainDispatcherRule
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SettingsViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val settingsAnalytics: SettingsAnalytics = mockk()
    private val appInfoProvider: AppInfoProvider = mockk()

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        every { appInfoProvider.isFdroidBuild() } returns true

        viewModel = SettingsViewModel(
            settingsAnalytics = settingsAnalytics,
            appInfoProvider = appInfoProvider
        )
    }

    @Test
    fun `initial state should have correct isFdroidBuild value`() {
        assertTrue(viewModel.viewState.value.isFdroidBuild)
    }

    @Test
    fun `trackScreenStart should call settingsAnalytics trackSettingsScreenStart`() {
        every { settingsAnalytics.trackSettingsScreenStart() } just Runs

        viewModel.viewState.value.trackScreenStart()

        verify { settingsAnalytics.trackSettingsScreenStart() }
    }

    @Test
    fun `viewModel should initialize with correct values from appInfoProvider`() {
        every { appInfoProvider.isFdroidBuild() } returns false

        val newViewModel = SettingsViewModel(
            settingsAnalytics = settingsAnalytics,
            appInfoProvider = appInfoProvider
        )

        assertEquals(false, newViewModel.viewState.value.isFdroidBuild)
    }
}
