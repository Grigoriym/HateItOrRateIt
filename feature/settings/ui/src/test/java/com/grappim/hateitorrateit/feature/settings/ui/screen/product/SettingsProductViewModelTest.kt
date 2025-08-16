package com.grappim.hateitorrateit.feature.settings.ui.screen.product

import com.grappim.hateitorrateit.data.analyticsapi.SettingsAnalytics
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
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

class SettingsProductViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val localDataStorage: LocalDataStorage = mockk()
    private val settingsAnalytics: SettingsAnalytics = mockk()

    private lateinit var viewModel: SettingsProductViewModel

    @Before
    fun setup() {
        every { localDataStorage.typeFlow } returns flowOf(HateRateType.HATE)
        coEvery { localDataStorage.changeTypeTo(any()) } just Runs

        viewModel = SettingsProductViewModel(
            localDataStorage = localDataStorage,
            settingsAnalytics = settingsAnalytics
        )
    }

    @Test
    fun `initial state should reflect type from localDataStorage typeFlow`() = runTest {
        assertEquals(HateRateType.HATE, viewModel.viewState.value.type)
    }

    @Test
    fun `setNewType should change type from HATE to RATE and call analytics`() = runTest {
        every { settingsAnalytics.trackDefaultTypeChangedTo(any()) } just Runs

        viewModel.viewState.value.setNewType()

        coVerify { localDataStorage.changeTypeTo(HateRateType.RATE) }
        verify { settingsAnalytics.trackDefaultTypeChangedTo(HateRateType.RATE.name) }
    }

    @Test
    fun `setNewType should track analytics with correct type name`() = runTest {
        every { settingsAnalytics.trackDefaultTypeChangedTo(any()) } just Runs

        viewModel.viewState.value.setNewType()

        verify { settingsAnalytics.trackDefaultTypeChangedTo("RATE") }
    }
}
