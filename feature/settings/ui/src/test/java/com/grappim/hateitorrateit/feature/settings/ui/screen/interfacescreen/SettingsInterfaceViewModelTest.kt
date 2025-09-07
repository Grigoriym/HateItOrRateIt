package com.grappim.hateitorrateit.feature.settings.ui.screen.interfacescreen

import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.localdatastorageapi.models.DarkThemeConfig
import com.grappim.hateitorrateit.feature.settings.ui.optionsgenerator.LocaleOptionsGenerator
import com.grappim.hateitorrateit.testing.core.MainDispatcherRule
import com.grappim.hateitorrateit.utils.ui.NativeText
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class SettingsInterfaceViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val localDataStorage: LocalDataStorage = mockk()
    private val localeOptionsGenerator: LocaleOptionsGenerator = mockk()

    private lateinit var viewModel: SettingsInterfaceViewModel

    private val testLocaleOptions: ImmutableMap<NativeText, String> = persistentMapOf(
        NativeText.Simple("English") to "en",
        NativeText.Simple("Deutsch") to "de"
    )

    @Before
    fun setup() {
        every { localDataStorage.darkThemeConfig } returns flowOf(DarkThemeConfig.LIGHT)
        coEvery { localDataStorage.setDarkThemeConfig(any()) } just Runs
        every { localeOptionsGenerator.getLocaleOptions() } returns testLocaleOptions

        viewModel = SettingsInterfaceViewModel(
            localDataStorage = localDataStorage,
            localeOptionsGenerator = localeOptionsGenerator
        )
    }

    @Test
    fun `initial state should have correct locale options`() {
        assertEquals(testLocaleOptions, viewModel.viewState.value.localeOptions)
    }

    @Test
    fun `initial state should reflect dark theme config from localDataStorage`() {
        assertEquals(DarkThemeConfig.LIGHT, viewModel.viewState.value.darkThemeConfig)
    }

    @Test
    fun `darkThemeConfig flow should update viewState`() = runTest {
        every { localDataStorage.darkThemeConfig } returns flowOf(DarkThemeConfig.DARK)

        val newViewModel = SettingsInterfaceViewModel(
            localDataStorage = localDataStorage,
            localeOptionsGenerator = localeOptionsGenerator
        )

        assertEquals(DarkThemeConfig.DARK, newViewModel.viewState.value.darkThemeConfig)
    }

    @Test
    fun `onDarkThemeConfigClicked should call localDataStorage setDarkThemeConfig`() = runTest {
        val newConfig = DarkThemeConfig.DARK

        viewModel.viewState.value.onDarkThemeConfigClicked(newConfig)

        coVerify { localDataStorage.setDarkThemeConfig(newConfig) }
    }

    @Test
    fun `onDarkThemeConfigClicked with FOLLOW_SYSTEM should call localDataStorage setDarkThemeConfig`() =
        runTest {
            val newConfig = DarkThemeConfig.FOLLOW_SYSTEM

            viewModel.viewState.value.onDarkThemeConfigClicked(newConfig)

            coVerify { localDataStorage.setDarkThemeConfig(newConfig) }
        }

    @Test
    fun `viewModel should initialize with default dark theme config when no initial value`() {
        every { localDataStorage.darkThemeConfig } returns flowOf(DarkThemeConfig.default())

        val newViewModel = SettingsInterfaceViewModel(
            localDataStorage = localDataStorage,
            localeOptionsGenerator = localeOptionsGenerator
        )

        assertEquals(DarkThemeConfig.default(), newViewModel.viewState.value.darkThemeConfig)
    }
}
