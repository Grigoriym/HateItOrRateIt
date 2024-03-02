package com.grappim.hateitorrateit.ui.screens.settings

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsapi.SettingsScreenAnalytics
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.domain.DarkThemeConfig
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.testing.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SettingsViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val dataCleaner: DataCleaner = mockk()
    private val localDataStorage: LocalDataStorage = mockk()
    private val analyticsController: AnalyticsController = mockk()
    private val settingsScreenAnalytics: SettingsScreenAnalytics = mockk()

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        every { localDataStorage.typeFlow } returns flowOf(HateRateType.RATE)
        coEvery { localDataStorage.changeTypeTo(any()) } just Runs

        every { localDataStorage.analyticsCollectionEnabled } returns flowOf(false)
        every { analyticsController.toggleAnalyticsCollection(any()) } just Runs
        coEvery { localDataStorage.setAnalyticsCollectionEnabled(any()) } just Runs

        every { localDataStorage.crashesCollectionEnabled } returns flowOf(false)
        every { analyticsController.toggleCrashesCollection(any()) } just Runs
        coEvery { localDataStorage.setCrashesCollectionEnabled(any()) } just Runs

        every { localDataStorage.darkThemeConfig } returns flowOf(DarkThemeConfig.default())
        coEvery { localDataStorage.setDarkThemeConfig(any()) } just Runs

        viewModel = SettingsViewModel(
            dataCleaner = dataCleaner,
            localDataStorage = localDataStorage,
            analyticsController = analyticsController,
            settingsScreenAnalytics = settingsScreenAnalytics
        )
    }

    @Test
    fun `on setNewType should change previous state to a new one`() {
        val newType = HateRateType.HATE

        assertEquals(viewModel.viewState.value.type, HateRateType.RATE)

        every { settingsScreenAnalytics.trackDefaultTypeChangedTo(any()) } just Runs
        every { localDataStorage.typeFlow } returns flowOf(newType)

        viewModel.viewState.value.setNewType()

        coVerify { localDataStorage.changeTypeTo(newType) }
        verify { settingsScreenAnalytics.trackDefaultTypeChangedTo(newType) }
    }

    @Test
    fun `on onClearDataClicked should show alert dialog`() {
        assertFalse(viewModel.viewState.value.showAlertDialog)

        viewModel.viewState.value.onClearDataClicked()

        assertTrue(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun `onAlertDialogConfirmButtonClicked should call dataCleaner`() = runTest {
        every { settingsScreenAnalytics.trackAllDataClearedConfirm() } just Runs
        coEvery { dataCleaner.clearAllData() } just Runs

        viewModel.viewState.value.onAlertDialogConfirmButtonClicked()

        verify { settingsScreenAnalytics.trackAllDataClearedConfirm() }
        coVerify { dataCleaner.clearAllData() }
        assertFalse(viewModel.viewState.value.isLoading)
    }

    @Test
    fun `with showAlertDialog true, onDismissDialog should set showAlertDialog to false`() {
        viewModel.viewState.value.onClearDataClicked()

        assertTrue(viewModel.viewState.value.showAlertDialog)

        viewModel.viewState.value.onDismissDialog()

        assertFalse(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun `onCrashlyticsToggle toggles crashes collection`() = runTest {
        viewModel.viewState.value.onCrashlyticsToggle()

        coVerify { localDataStorage.setCrashesCollectionEnabled(true) }
    }

    @Test
    fun `onAnalyticsToggle should toggle analytics collection`() = runTest {
        viewModel.viewState.value.onAnalyticsToggle()

        coVerify { localDataStorage.setAnalyticsCollectionEnabled(true) }
    }

    @Test
    fun `on trackScreenStart should call trackSettingsScreenStart event`() {
        every { settingsScreenAnalytics.trackSettingsScreenStart() } just Runs

        viewModel.viewState.value.trackScreenStart()

        verify { settingsScreenAnalytics.trackSettingsScreenStart() }
    }

    @Test
    fun `onDarkThemeConfigClicked should set config in localDataStorage`() = runTest {
        val config = DarkThemeConfig.DARK
        viewModel.viewState.value.onDarkThemeConfigClicked(config)

        coVerify { localDataStorage.setDarkThemeConfig(config) }
    }
}
