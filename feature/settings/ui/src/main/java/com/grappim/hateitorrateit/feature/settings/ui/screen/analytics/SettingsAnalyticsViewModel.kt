package com.grappim.hateitorrateit.feature.settings.ui.screen.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import com.grappim.hateitorrateit.data.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsAnalyticsViewModel @Inject constructor(
    private val localDataStorage: LocalDataStorage,
    private val analyticsController: AnalyticsController
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SettingsAnalyticsViewState(
            onCrashlyticsToggle = ::onCrashlyticsToggle,
            onAnalyticsToggle = ::onAnalyticsToggle
        )
    )
    val viewState = _viewState.asStateFlow()

    init {
        localDataStorage.crashesCollectionEnabled.onEach { value ->
            analyticsController.toggleCrashesCollection(value)
            _viewState.update {
                it.copy(isCrashesCollectionEnabled = value)
            }
        }.launchIn(viewModelScope)

        localDataStorage.analyticsCollectionEnabled.onEach { value ->
            analyticsController.toggleAnalyticsCollection(value)
            _viewState.update {
                it.copy(isAnalyticsCollectionEnabled = value)
            }
        }.launchIn(viewModelScope)
    }

    private fun onCrashlyticsToggle() {
        viewModelScope.launch {
            localDataStorage.setCrashesCollectionEnabled(
                viewState.value.isCrashesCollectionEnabled.not()
            )
        }
    }

    private fun onAnalyticsToggle() {
        viewModelScope.launch {
            localDataStorage.setAnalyticsCollectionEnabled(
                viewState.value.isAnalyticsCollectionEnabled.not()
            )
        }
    }
}
