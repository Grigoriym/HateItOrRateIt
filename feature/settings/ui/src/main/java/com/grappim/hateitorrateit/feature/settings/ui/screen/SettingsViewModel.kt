package com.grappim.hateitorrateit.feature.settings.ui.screen

import androidx.lifecycle.ViewModel
import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import com.grappim.hateitorrateit.data.analyticsapi.SettingsAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsAnalytics: SettingsAnalytics,
    appInfoProvider: AppInfoProvider
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SettingsViewState(
            trackScreenStart = ::trackScreenStart,
            isFdroidBuild = appInfoProvider.isFdroidBuild()
        )
    )
    val viewState = _viewState.asStateFlow()

    private fun trackScreenStart() {
        settingsAnalytics.trackSettingsScreenStart()
    }
}
