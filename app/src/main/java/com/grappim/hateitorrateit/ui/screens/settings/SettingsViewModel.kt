package com.grappim.hateitorrateit.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.core.DataCleaner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataCleaner: DataCleaner,
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SettingsViewState(
            isLoading = false,
        )
    )
    val viewState = _viewState.asStateFlow()

    fun clearData() {
        viewModelScope.launch {
            _viewState.update {
                it.copy(isLoading = true)
            }
            dataCleaner.clearAllData()
            _viewState.update {
                it.copy(isLoading = false)
            }
        }
    }
}