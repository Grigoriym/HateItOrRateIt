package com.grappim.hateitorrateit.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.domain.HateRateType
import com.grappim.hateitorrateit.core.DataCleaner
import com.grappim.hateitorrateit.data.storage.local.LocalDataStorage
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
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SettingsViewState(
            setType = ::setNewType,
            onClearDataClicked = ::askIfShouldClearData,
            onAlertDialogConfirmButtonClicked = ::clearData,
            onDismissDialog = ::dismissDialog,
        )
    )
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            localDataStorage.typeFlow.collect { value ->
                _viewState.update {
                    it.copy(type = value)
                }
            }
        }
    }

    private fun setNewType() {
        viewModelScope.launch {
            localDataStorage.changeTypeTo(HateRateType.changeType(_viewState.value.type))
        }
    }

    private fun askIfShouldClearData() {
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
        viewModelScope.launch {
            _viewState.update {
                it.copy(isLoading = true, showAlertDialog = false)
            }
            dataCleaner.clearAllData()
            _viewState.update {
                it.copy(isLoading = false)
            }
        }
    }
}