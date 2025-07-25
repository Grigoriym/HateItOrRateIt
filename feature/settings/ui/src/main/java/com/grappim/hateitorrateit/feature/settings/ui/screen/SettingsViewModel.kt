package com.grappim.hateitorrateit.feature.settings.ui.screen

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import com.grappim.hateitorrateit.data.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.data.analyticsapi.SettingsAnalytics
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.localdatastorageapi.models.DarkThemeConfig
import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigsListener
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.feature.settings.ui.optionsgenerator.LocaleOptionsGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataCleaner: DataCleaner,
    private val localDataStorage: LocalDataStorage,
    private val analyticsController: AnalyticsController,
    private val settingsAnalytics: SettingsAnalytics,
    private val remoteConfigsListener: RemoteConfigsListener,
    appInfoProvider: AppInfoProvider,
    localeOptionsGenerator: LocaleOptionsGenerator
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
            onDarkThemeConfigClicked = ::onDarkThemeConfigClicked,
            localeOptions = localeOptionsGenerator.getLocaleOptions(),
            appInfo = appInfoProvider.getAppInfo(),
            isFdroidBuild = appInfoProvider.isFdroidBuild()
        )
    )
    val viewState = _viewState.asStateFlow()

    init {
        localDataStorage.typeFlow.onEach { value ->
            _viewState.update {
                it.copy(type = value)
            }
        }.launchIn(viewModelScope)

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

        localDataStorage.darkThemeConfig.onEach { value ->
            _viewState.update {
                it.copy(darkThemeConfig = value)
            }
        }.launchIn(viewModelScope)

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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun clearRemoteConfigs() {
        remoteConfigsListener.onClose()
    }

    override fun onCleared() {
        super.onCleared()
        clearRemoteConfigs()
    }

    private fun onDarkThemeConfigClicked(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            localDataStorage.setDarkThemeConfig(darkThemeConfig)
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
        settingsAnalytics.trackSettingsScreenStart()
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
        settingsAnalytics.trackDefaultTypeChangedTo(newType.name)
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
        settingsAnalytics.trackAllDataClearedConfirm()

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
