package com.grappim.hateitorrateit.feature.settings.ui.screen.analytics

import com.grappim.hateitorrateit.data.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.testing.core.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsAnalyticsViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val localDataStorage: LocalDataStorage = mockk()
    private val analyticsController: AnalyticsController = mockk()

    private lateinit var viewModel: SettingsAnalyticsViewModel

    @Before
    fun setup() {
        every { localDataStorage.crashesCollectionEnabled } returns flowOf(false)
        every { localDataStorage.analyticsCollectionEnabled } returns flowOf(false)
        every { analyticsController.toggleCrashesCollection(any()) } just Runs
        every { analyticsController.toggleAnalyticsCollection(any()) } just Runs
        coEvery { localDataStorage.setCrashesCollectionEnabled(any()) } just Runs
        coEvery { localDataStorage.setAnalyticsCollectionEnabled(any()) } just Runs

        viewModel = SettingsAnalyticsViewModel(
            localDataStorage = localDataStorage,
            analyticsController = analyticsController
        )
    }

    @Test
    fun `initial state should reflect crashes collection enabled from localDataStorage`() {
        assertFalse(viewModel.viewState.value.isCrashesCollectionEnabled)
        verify { analyticsController.toggleCrashesCollection(false) }
    }

    @Test
    fun `initial state should reflect analytics collection enabled from localDataStorage`() {
        assertFalse(viewModel.viewState.value.isAnalyticsCollectionEnabled)
        verify { analyticsController.toggleAnalyticsCollection(false) }
    }

    @Test
    fun `crashes collection flow changes should update viewState and call analyticsController`() =
        runTest {
            every { localDataStorage.crashesCollectionEnabled } returns flowOf(true)

            val newViewModel = SettingsAnalyticsViewModel(
                localDataStorage = localDataStorage,
                analyticsController = analyticsController
            )

            assertTrue(newViewModel.viewState.value.isCrashesCollectionEnabled)
            verify { analyticsController.toggleCrashesCollection(true) }
        }

    @Test
    fun `analytics collection flow changes should update viewState and call analyticsController`() =
        runTest {
            every { localDataStorage.analyticsCollectionEnabled } returns flowOf(true)

            val newViewModel = SettingsAnalyticsViewModel(
                localDataStorage = localDataStorage,
                analyticsController = analyticsController
            )

            assertTrue(newViewModel.viewState.value.isAnalyticsCollectionEnabled)
            verify { analyticsController.toggleAnalyticsCollection(true) }
        }

    @Test
    fun `onCrashlyticsToggle should toggle crashes collection enabled state`() = runTest {
        // Current state is false, toggle should set to true
        viewModel.viewState.value.onCrashlyticsToggle()

        coVerify { localDataStorage.setCrashesCollectionEnabled(true) }
    }

    @Test
    fun `onCrashlyticsToggle should toggle from true to false`() = runTest {
        every { localDataStorage.crashesCollectionEnabled } returns flowOf(true)

        val trueViewModel = SettingsAnalyticsViewModel(
            localDataStorage = localDataStorage,
            analyticsController = analyticsController
        )

        // Current state is true, toggle should set to false
        trueViewModel.viewState.value.onCrashlyticsToggle()

        coVerify { localDataStorage.setCrashesCollectionEnabled(false) }
    }

    @Test
    fun `onAnalyticsToggle should toggle analytics collection enabled state`() = runTest {
        // Current state is false, toggle should set to true
        viewModel.viewState.value.onAnalyticsToggle()

        coVerify { localDataStorage.setAnalyticsCollectionEnabled(true) }
    }

    @Test
    fun `onAnalyticsToggle should toggle from true to false`() = runTest {
        every { localDataStorage.analyticsCollectionEnabled } returns flowOf(true)

        val trueViewModel = SettingsAnalyticsViewModel(
            localDataStorage = localDataStorage,
            analyticsController = analyticsController
        )

        // Current state is true, toggle should set to false
        trueViewModel.viewState.value.onAnalyticsToggle()

        coVerify { localDataStorage.setAnalyticsCollectionEnabled(false) }
    }

    @Test
    fun `viewModel should handle both flows updating simultaneously`() = runTest {
        every { localDataStorage.crashesCollectionEnabled } returns flowOf(true)
        every { localDataStorage.analyticsCollectionEnabled } returns flowOf(true)

        val bothTrueViewModel = SettingsAnalyticsViewModel(
            localDataStorage = localDataStorage,
            analyticsController = analyticsController
        )

        assertTrue(bothTrueViewModel.viewState.value.isCrashesCollectionEnabled)
        assertTrue(bothTrueViewModel.viewState.value.isAnalyticsCollectionEnabled)
        verify { analyticsController.toggleCrashesCollection(true) }
        verify { analyticsController.toggleAnalyticsCollection(true) }
    }
}
