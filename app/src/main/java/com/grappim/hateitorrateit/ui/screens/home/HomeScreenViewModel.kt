package com.grappim.hateitorrateit.ui.screens.home

import androidx.compose.runtime.MutableState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.core.WhileViewSubscribed
import com.grappim.hateitorrateit.data.DocsRepository
import com.grappim.hateitorrateit.model.UiModelsMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val SEARCH_QUERY_KEY = "search.query.key"

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    docsRepository: DocsRepository,
    private val uiModelsMapper: UiModelsMapper,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

//    private val _viewState = MutableStateFlow(
//        HomeViewState(
//            query = "",
//            onSearchQueryChanged = ::onSearchQueryChanged,
//            onClearQueryClicked = {},
//            docs = emptyList(),
//        )
//    )

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

//    val viewState = _viewState.asStateFlow()

//    val searchQuery = savedStateHandle.getStateFlow(
//        key = SEARCH_QUERY_KEY,
//        initialValue = ""
//    )

    val docs = query
        .flatMapLatest {
            docsRepository.getAllDocsFlow(it)
        }.map { list ->
            list.map { document ->
                uiModelsMapper.toDocumentUi(document)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = WhileViewSubscribed,
            initialValue = emptyList(),
        )

//    val docs = docsRepository.getAllDocsFlow()
//        .map { list ->
//            list.map { document ->
//                uiModelsMapper.toDocumentUi(document)
//            }
//        }
//        .stateIn(
//            scope = viewModelScope,
//            started = WhileViewSubscribed,
//            initialValue = emptyList(),
//        )

    fun onSearchQueryChanged(query: String) {
        _query.value = query
    }

    fun clearQuery() {
        _query.value = ""
    }
}
