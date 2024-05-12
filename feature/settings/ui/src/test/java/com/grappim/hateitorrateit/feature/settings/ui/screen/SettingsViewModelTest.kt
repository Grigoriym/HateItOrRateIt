package com.grappim.hateitorrateit.feature.settings.ui.screen

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsapi.SettingsAnalytics
import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigsListener
import com.grappim.hateitorrateit.domain.DarkThemeConfig
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.feature.settings.ui.optionsgenerator.LocaleOptionsGenerator
import com.grappim.hateitorrateit.testing.MainDispatcherRule
import com.grappim.hateitorrateit.testing.testException
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
    private val settingsAnalytics: SettingsAnalytics = mockk()
    private val remoteConfigsListener: RemoteConfigsListener = mockk()
    private val localeOptionsGenerator: LocaleOptionsGenerator = mockk()
    private val appInfoProvider: AppInfoProvider = mockk()

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

        every { remoteConfigsListener.githubRepoLink } returns flowOf("github")
        every { remoteConfigsListener.privacyPolicy } returns flowOf("privacy policy")
        every { localeOptionsGenerator.getLocaleOptions() } returns mapOf()

        every { appInfoProvider.getAppInfo() } returns "appInfo"

        viewModel = SettingsViewModel(
            dataCleaner = dataCleaner,
            localDataStorage = localDataStorage,
            analyticsController = analyticsController,
            settingsAnalytics = settingsAnalytics,
            remoteConfigsListener = remoteConfigsListener,
            localeOptionsGenerator = localeOptionsGenerator,
            appInfoProvider = appInfoProvider
        )
    }

    @Test
    fun `clearRemoteConfigs should close remoteConfigs`() {
        every { remoteConfigsListener.onClose() } just Runs

        viewModel.clearRemoteConfigs()

        verify { remoteConfigsListener.onClose() }
    }

    @Test
    fun `on setNewType should change previous state to a new one`() {
        val newType = HateRateType.HATE

        assertEquals(viewModel.viewState.value.type, HateRateType.RATE)

        every { settingsAnalytics.trackDefaultTypeChangedTo(any()) } just Runs
        every { localDataStorage.typeFlow } returns flowOf(newType)

        viewModel.viewState.value.setNewType()

        coVerify { localDataStorage.changeTypeTo(newType) }
        verify { settingsAnalytics.trackDefaultTypeChangedTo(newType) }
    }

    @Test
    fun `on onClearDataClicked should show alert dialog`() {
        assertFalse(viewModel.viewState.value.showAlertDialog)

        viewModel.viewState.value.onClearDataClicked()

        assertTrue(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun `on onAlertDialogConfirmButtonClicked with clearAllData failure should show set isLoading as false`() {
        every { settingsAnalytics.trackAllDataClearedConfirm() } just Runs
        coEvery { dataCleaner.clearAllData() } throws testException

        assertFalse(viewModel.viewState.value.showAlertDialog)
        assertFalse(viewModel.viewState.value.isLoading)

        viewModel.viewState.value.onAlertDialogConfirmButtonClicked()

        verify { settingsAnalytics.trackAllDataClearedConfirm() }
        coVerify { dataCleaner.clearAllData() }

        assertFalse(viewModel.viewState.value.showAlertDialog)
        assertFalse(viewModel.viewState.value.isLoading)
    }

    @Test
    fun `onAlertDialogConfirmButtonClicked should call dataCleaner`() = runTest {
        every { settingsAnalytics.trackAllDataClearedConfirm() } just Runs
        coEvery { dataCleaner.clearAllData() } just Runs

        viewModel.viewState.value.onAlertDialogConfirmButtonClicked()

        verify { settingsAnalytics.trackAllDataClearedConfirm() }
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
        every { settingsAnalytics.trackSettingsScreenStart() } just Runs

        viewModel.viewState.value.trackScreenStart()

        verify { settingsAnalytics.trackSettingsScreenStart() }
    }

    @Test
    fun `onDarkThemeConfigClicked should set config in localDataStorage`() = runTest {
        val config = DarkThemeConfig.DARK
        viewModel.viewState.value.onDarkThemeConfigClicked(config)

        coVerify { localDataStorage.setDarkThemeConfig(config) }
    }
}
