package com.grappim.hateitorrateit.feature.settings.ui.screen.interfacescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.localdatastorageapi.models.DarkThemeConfig
import com.grappim.hateitorrateit.feature.settings.ui.optionsgenerator.LocaleOptionsGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsInterfaceViewModel @Inject constructor(
    private val localDataStorage: LocalDataStorage,
    localeOptionsGenerator: LocaleOptionsGenerator
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SettingsInterfaceViewState(
            onDarkThemeConfigClicked = ::onDarkThemeConfigClicked,
            localeOptions = localeOptionsGenerator.getLocaleOptions()
        )
    )
    val viewState = _viewState.asStateFlow()

    init {
        localDataStorage.darkThemeConfig.onEach { value ->
            _viewState.update {
                it.copy(darkThemeConfig = value)
            }
        }.launchIn(viewModelScope)
    }

    private fun onDarkThemeConfigClicked(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            localDataStorage.setDarkThemeConfig(darkThemeConfig)
        }
    }
}
