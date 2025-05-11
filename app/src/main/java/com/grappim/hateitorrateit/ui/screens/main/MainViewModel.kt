package com.grappim.hateitorrateit.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.data.cleanerapi.EmptyFilesCleaner
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.localdatastorageapi.models.DarkThemeConfig
import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigsListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val emptyFilesCleaner: EmptyFilesCleaner,
    private val localDataStorage: LocalDataStorage,
    remoteConfigsListener: RemoteConfigsListener
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        MainActivityViewState(
            darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM
        )
    )

    val viewState = _viewState.asStateFlow()

    val inAppUpdateEnabled = remoteConfigsListener.inAppUpdateEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    init {
        viewModelScope.launch {
            launch {
                emptyFilesCleaner.clean()
            }

            launch {
                localDataStorage.darkThemeConfig.collect { value ->
                    _viewState.update {
                        it.copy(darkThemeConfig = value)
                    }
                }
            }
        }
    }
}
