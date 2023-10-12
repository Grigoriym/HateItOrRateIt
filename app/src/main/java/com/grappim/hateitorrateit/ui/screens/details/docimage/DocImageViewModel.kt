package com.grappim.hateitorrateit.ui.screens.details.docimage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.core.navigation.RootNavDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class DocImageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val uri =
        checkNotNull(savedStateHandle.get<String>(RootNavDestinations.DetailsImage.KEY))

    private val viewModelState = MutableStateFlow(
        DocImageViewModelState(
            uri = URLDecoder.decode(uri, StandardCharsets.UTF_8.toString())
        )
    )

    val uiState = viewModelState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value
        )
}

data class DocImageViewModelState(
    val uri: String
)