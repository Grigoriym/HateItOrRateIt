package com.grappim.hateitorrateit.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.domain.HateRateType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataCleaner: DataCleaner,
    private val localDataStorage: LocalDataStorage,
    private val analyticsController: AnalyticsController,
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SettingsViewState(
            setType = ::setNewType,
            onClearDataClicked = ::askIfShouldClearData,
            onAlertDialogConfirmButtonClicked = ::clearData,
            onDismissDialog = ::dismissDialog,
            onCrashlyticsToggle = ::onCrashlyticsToggle
        )
    )
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                localDataStorage.typeFlow.collect { value ->
                    _viewState.update {
                        it.safeCopy(type = value)
                    }
                }
            }
            launch {
                localDataStorage.crashesCollectionEnabled.collect { value ->
                    analyticsController.toggleCrashesCollection(value)
                    _viewState.update {
                        it.safeCopy(isCrashesCollectionEnabled = value)
                    }
                }
            }
        }
    }

    private fun onCrashlyticsToggle() {
        viewModelScope.launch {
            localDataStorage.setCrashesCollectionEnabled(viewState.value.isCrashesCollectionEnabled.not())
        }
    }

    private fun setNewType() {
        viewModelScope.launch {
            localDataStorage.changeTypeTo(HateRateType.changeType(_viewState.value.type))
        }
    }

    private fun askIfShouldClearData() {
        _viewState.update {
            it.safeCopy(showAlertDialog = true)
        }
    }

    private fun dismissDialog() {
        _viewState.update {
            it.safeCopy(showAlertDialog = false)
        }
    }

    private fun clearData() {
        viewModelScope.launch {
            _viewState.update {
                it.safeCopy(isLoading = true, showAlertDialog = false)
            }
            dataCleaner.clearAllData()
            _viewState.update {
                it.safeCopy(isLoading = false)
            }
        }
    }
}
