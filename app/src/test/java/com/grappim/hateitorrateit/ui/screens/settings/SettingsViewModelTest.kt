package com.grappim.hateitorrateit.ui.screens.settings

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.testing.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

class SettingsViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val dataCleaner: DataCleaner = mockk()
    private val localDataStorage: LocalDataStorage = mockk()
    private val analyticsController: AnalyticsController = mockk()

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        every { localDataStorage.typeFlow } returns flowOf(HateRateType.RATE)
        every { localDataStorage.crashesCollectionEnabled } returns flowOf(false)
        every { analyticsController.toggleCrashesCollection(any()) } just Runs
        coEvery { localDataStorage.setCrashesCollectionEnabled(any()) } just Runs
        coEvery { localDataStorage.changeTypeTo(any()) } just Runs

        viewModel = SettingsViewModel(
            dataCleaner = dataCleaner,
            localDataStorage = localDataStorage,
            analyticsController = analyticsController,
        )
    }

    @Test
    fun `setNewType sets new type`() {
        viewModel.viewState.value.setType()
        coVerify { localDataStorage.changeTypeTo(any()) }
    }

    @Test
    fun `askIfShouldClearData sets dialog state`() {
        viewModel.viewState.value.onClearDataClicked()
        assertTrue(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun `onAlertDialogConfirmButtonClicked calls dataCleaner`() = runTest {
        coEvery { dataCleaner.clearAllData() } just Runs

        viewModel.viewState.value.onAlertDialogConfirmButtonClicked()

        coVerify { dataCleaner.clearAllData() }
        assertFalse(viewModel.viewState.value.isLoading)
    }

    @Test
    fun `onCrashlyticsToggle toggles crashes collection`() = runTest {
        viewModel.viewState.value.onCrashlyticsToggle()
        coVerify { localDataStorage.setCrashesCollectionEnabled(any()) }
    }

    @Test
    fun `onDismissDialog sets dialog state`() {
        viewModel.viewState.value.onDismissDialog()
        assertFalse(viewModel.viewState.value.showAlertDialog)
    }
}
