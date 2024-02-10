package com.grappim.hateitorrateit.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsapi.SettingsScreenAnalytics
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.domain.HateRateType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataCleaner: DataCleaner,
    private val localDataStorage: LocalDataStorage,
    private val analyticsController: AnalyticsController,
    private val settingsScreenAnalytics: SettingsScreenAnalytics,
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SettingsViewState(
            setNewType = ::setNewType,
            onClearDataClicked = ::onClearDataClicked,
            onAlertDialogConfirmButtonClicked = ::clearData,
            onDismissDialog = ::dismissDialog,
            onCrashlyticsToggle = ::onCrashlyticsToggle,
            onAnalyticsToggle = ::onAnalyticsToggle,
            trackScreenStart = ::trackScreenStart,
        )
    )
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                localDataStorage.typeFlow.collect { value ->
                    _viewState.update {
                        it.copy(type = value)
                    }
                }
            }
            launch {
                localDataStorage.crashesCollectionEnabled.collect { value ->
                    analyticsController.toggleCrashesCollection(value)
                    _viewState.update {
                        it.copy(isCrashesCollectionEnabled = value)
                    }
                }
            }
            launch {
                localDataStorage.analyticsCollectionEnabled.collect { value ->
                    analyticsController.toggleAnalyticsCollection(value)
                    _viewState.update {
                        it.copy(isAnalyticsCollectionEnabled = value)
                    }
                }
            }
        }
    }

    private fun onAnalyticsToggle() {
        viewModelScope.launch {
            localDataStorage.setAnalyticsCollectionEnabled(
                viewState.value.isAnalyticsCollectionEnabled.not()
            )
        }
    }

    private fun trackScreenStart() {
        settingsScreenAnalytics.trackSettingsScreenStart()
    }

    private fun onCrashlyticsToggle() {
        viewModelScope.launch {
            localDataStorage.setCrashesCollectionEnabled(
                viewState.value.isCrashesCollectionEnabled.not()
            )
        }
    }

    private fun setNewType() {
        val newType = HateRateType.changeType(_viewState.value.type)
        settingsScreenAnalytics.trackDefaultTypeChangedTo(newType)
        viewModelScope.launch {
            localDataStorage.changeTypeTo(newType)
        }
    }

    private fun onClearDataClicked() {
        _viewState.update {
            it.copy(showAlertDialog = true)
        }
    }

    private fun dismissDialog() {
        _viewState.update {
            it.copy(showAlertDialog = false)
        }
    }

    private fun clearData() {
        settingsScreenAnalytics.trackAllDataClearedConfirm()

        viewModelScope.launch {
            _viewState.update {
                it.copy(isLoading = true, showAlertDialog = false)
            }
            runCatching {
                dataCleaner.clearAllData()
            }.fold(
                onSuccess = {
                    _viewState.update {
                        it.copy(isLoading = false)
                    }
                },
                onFailure = { throwable ->
                    Timber.e(throwable)
                    _viewState.update {
                        it.copy(isLoading = false)
                    }
                }
            )
        }
    }
}
