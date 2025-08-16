package com.grappim.hateitorrateit.feature.settings.ui.screen.about

import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigsListener
import com.grappim.hateitorrateit.testing.core.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class SettingsAboutScreenViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val remoteConfigsListener: RemoteConfigsListener = mockk()
    private val appInfoProvider: AppInfoProvider = mockk()

    private lateinit var viewModel: SettingsAboutScreenViewModel

    private val testAppInfo = "HateItOrRateIt v1.0.0 (Build 123)"
    private val testGithubLink = "https://github.com/test/repo"
    private val testPrivacyPolicyLink = "https://example.com/privacy"

    @Before
    fun setup() {
        every { appInfoProvider.getAppInfo() } returns testAppInfo
        every { remoteConfigsListener.githubRepoLink } returns flowOf(testGithubLink)
        every { remoteConfigsListener.privacyPolicy } returns flowOf(testPrivacyPolicyLink)

        viewModel = SettingsAboutScreenViewModel(
            remoteConfigsListener = remoteConfigsListener,
            appInfoProvider = appInfoProvider
        )
    }

    @Test
    fun `initial state should have correct app info from appInfoProvider`() {
        assertEquals(testAppInfo, viewModel.viewState.value.appInfo)
    }

    @Test
    fun `initial state should reflect github repo link from remoteConfigsListener`() = runTest {
        assertEquals(testGithubLink, viewModel.viewState.value.githubRepoLink)
    }

    @Test
    fun `initial state should reflect privacy policy link from remoteConfigsListener`() = runTest {
        assertEquals(testPrivacyPolicyLink, viewModel.viewState.value.privacyPolicyLink)
    }

    @Test
    fun `githubRepoLink flow changes should update viewState`() = runTest {
        val newGithubLink = "https://github.com/new/repo"
        every { remoteConfigsListener.githubRepoLink } returns flowOf(newGithubLink)

        val newViewModel = SettingsAboutScreenViewModel(
            remoteConfigsListener = remoteConfigsListener,
            appInfoProvider = appInfoProvider
        )

        assertEquals(newGithubLink, newViewModel.viewState.value.githubRepoLink)
    }

    @Test
    fun `privacyPolicy flow changes should update viewState`() = runTest {
        val newPrivacyPolicyLink = "https://example.com/new-privacy"
        every { remoteConfigsListener.privacyPolicy } returns flowOf(newPrivacyPolicyLink)

        val newViewModel = SettingsAboutScreenViewModel(
            remoteConfigsListener = remoteConfigsListener,
            appInfoProvider = appInfoProvider
        )

        assertEquals(newPrivacyPolicyLink, newViewModel.viewState.value.privacyPolicyLink)
    }

    @Test
    fun `viewModel should handle empty remote config values`() = runTest {
        every { remoteConfigsListener.githubRepoLink } returns flowOf("")
        every { remoteConfigsListener.privacyPolicy } returns flowOf("")

        val emptyConfigViewModel = SettingsAboutScreenViewModel(
            remoteConfigsListener = remoteConfigsListener,
            appInfoProvider = appInfoProvider
        )

        assertEquals("", emptyConfigViewModel.viewState.value.githubRepoLink)
        assertEquals("", emptyConfigViewModel.viewState.value.privacyPolicyLink)
        assertEquals(testAppInfo, emptyConfigViewModel.viewState.value.appInfo)
    }

    @Test
    fun `viewModel should handle different app info values`() = runTest {
        val differentAppInfo = "DifferentApp v2.0.0 (Build 456)"
        every { appInfoProvider.getAppInfo() } returns differentAppInfo

        val differentAppInfoViewModel = SettingsAboutScreenViewModel(
            remoteConfigsListener = remoteConfigsListener,
            appInfoProvider = appInfoProvider
        )

        assertEquals(differentAppInfo, differentAppInfoViewModel.viewState.value.appInfo)
    }

    @Test
    fun `viewModel should properly initialize with all remote config flows`() = runTest {
        val state = viewModel.viewState.value

        assertEquals(testAppInfo, state.appInfo)
        assertEquals(testGithubLink, state.githubRepoLink)
        assertEquals(testPrivacyPolicyLink, state.privacyPolicyLink)
    }
}
