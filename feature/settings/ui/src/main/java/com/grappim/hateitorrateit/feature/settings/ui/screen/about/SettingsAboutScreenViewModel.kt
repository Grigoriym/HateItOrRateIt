package com.grappim.hateitorrateit.feature.settings.ui.screen.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigsListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsAboutScreenViewModel @Inject constructor(
    remoteConfigsListener: RemoteConfigsListener,
    appInfoProvider: AppInfoProvider
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SettingsAboutScreenState(
            appInfo = appInfoProvider.getAppInfo()
        )
    )
    val viewState = _viewState.asStateFlow()

    init {
        remoteConfigsListener.githubRepoLink.onEach { value ->
            _viewState.update {
                it.copy(githubRepoLink = value)
            }
        }.launchIn(viewModelScope)

        remoteConfigsListener.privacyPolicy.onEach { value ->
            _viewState.update {
                it.copy(privacyPolicyLink = value)
            }
        }.launchIn(viewModelScope)
    }
}
