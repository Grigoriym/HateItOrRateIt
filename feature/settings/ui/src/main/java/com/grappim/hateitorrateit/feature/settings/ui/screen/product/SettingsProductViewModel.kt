package com.grappim.hateitorrateit.feature.settings.ui.screen.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.data.analyticsapi.SettingsAnalytics
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsProductViewModel @Inject constructor(
    private val localDataStorage: LocalDataStorage,
    private val settingsAnalytics: SettingsAnalytics
) : ViewModel() {

    val viewState = localDataStorage.typeFlow
        .map { type ->
            SettingsProductViewState(
                type = type,
                setNewType = ::setNewType
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsProductViewState(setNewType = ::setNewType)
        )

    private fun setNewType() {
        val newType = HateRateType.changeType(viewState.value.type)
        settingsAnalytics.trackDefaultTypeChangedTo(newType.name)
        viewModelScope.launch {
            localDataStorage.changeTypeTo(newType)
        }
    }
}
