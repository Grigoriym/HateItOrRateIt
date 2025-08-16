package com.grappim.hateitorrateit.feature.settings.ui.screen.database

import com.grappim.hateitorrateit.data.analyticsapi.SettingsAnalytics
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.testing.core.MainDispatcherRule
import com.grappim.hateitorrateit.testing.core.testException
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsDatabaseViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val dataCleaner: DataCleaner = mockk()
    private val settingsAnalytics: SettingsAnalytics = mockk()

    private lateinit var viewModel: SettingsDatabaseViewModel

    @Before
    fun setup() {
        viewModel = SettingsDatabaseViewModel(
            dataCleaner = dataCleaner,
            settingsAnalytics = settingsAnalytics
        )
    }

    @Test
    fun `initial state should have correct default values`() {
        val state = viewModel.viewState.value

        assertFalse(state.isLoading)
        assertFalse(state.showAlertDialog)
    }

    @Test
    fun `onClearDataClicked should show alert dialog`() {
        assertFalse(viewModel.viewState.value.showAlertDialog)

        viewModel.viewState.value.onClearDataClicked()

        assertTrue(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun `onDismissDialog should hide alert dialog`() {
        // First show the dialog
        viewModel.viewState.value.onClearDataClicked()
        assertTrue(viewModel.viewState.value.showAlertDialog)

        // Then dismiss it
        viewModel.viewState.value.onDismissDialog()

        assertFalse(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun `onAlertDialogConfirmButtonClicked should call dataCleaner and analytics`() = runTest {
        every { settingsAnalytics.trackAllDataClearedConfirm() } just Runs
        coEvery { dataCleaner.clearAllData() } just Runs

        viewModel.viewState.value.onAlertDialogConfirmButtonClicked()

        verify { settingsAnalytics.trackAllDataClearedConfirm() }
        coVerify { dataCleaner.clearAllData() }
        assertFalse(viewModel.viewState.value.isLoading)
        assertFalse(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun `onAlertDialogConfirmButtonClicked should set loading state correctly during operation`() =
        runTest {
            every { settingsAnalytics.trackAllDataClearedConfirm() } just Runs
            coEvery { dataCleaner.clearAllData() } just Runs

            // Show dialog first
            viewModel.viewState.value.onClearDataClicked()
            assertTrue(viewModel.viewState.value.showAlertDialog)

            viewModel.viewState.value.onAlertDialogConfirmButtonClicked()

            // After completion, loading should be false and dialog should be hidden
            assertFalse(viewModel.viewState.value.isLoading)
            assertFalse(viewModel.viewState.value.showAlertDialog)
        }

    @Test
    fun `onAlertDialogConfirmButtonClicked with dataCleaner failure should handle error gracefully`() =
        runTest {
            every { settingsAnalytics.trackAllDataClearedConfirm() } just Runs
            coEvery { dataCleaner.clearAllData() } throws testException

            viewModel.viewState.value.onAlertDialogConfirmButtonClicked()

            verify { settingsAnalytics.trackAllDataClearedConfirm() }
            coVerify { dataCleaner.clearAllData() }
            assertFalse(viewModel.viewState.value.isLoading)
            assertFalse(viewModel.viewState.value.showAlertDialog)
        }

    @Test
    fun `clearData flow should properly manage dialog and loading states`() = runTest {
        every { settingsAnalytics.trackAllDataClearedConfirm() } just Runs
        coEvery { dataCleaner.clearAllData() } just Runs

        // Initial state
        assertFalse(viewModel.viewState.value.showAlertDialog)
        assertFalse(viewModel.viewState.value.isLoading)

        // Show dialog
        viewModel.viewState.value.onClearDataClicked()
        assertTrue(viewModel.viewState.value.showAlertDialog)
        assertFalse(viewModel.viewState.value.isLoading)

        // Confirm clear data
        viewModel.viewState.value.onAlertDialogConfirmButtonClicked()

        // Final state - dialog hidden, not loading
        assertFalse(viewModel.viewState.value.showAlertDialog)
        assertFalse(viewModel.viewState.value.isLoading)
    }
}
