package com.grappim.hateitorrateit.feature.settings.ui.screen.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.data.analyticsapi.SettingsAnalytics
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsDatabaseViewModel @Inject constructor(
    private val dataCleaner: DataCleaner,
    private val settingsAnalytics: SettingsAnalytics
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SettingsDatabaseViewState(
            onClearDataClicked = ::onClearDataClicked,
            onAlertDialogConfirmButtonClicked = ::clearData,
            onDismissDialog = ::dismissDialog
        )
    )
    val viewState = _viewState.asStateFlow()

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
