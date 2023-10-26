package com.grappim.hateitorrateit.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.domain.HateRateType
import com.grappim.hateitorrateit.data.DocsRepository
import com.grappim.hateitorrateit.model.UiModelsMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val docsRepository: DocsRepository,
    private val uiModelsMapper: UiModelsMapper,
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        HomeViewState(
            onSearchQueryChanged = ::onSearchQueryChanged,
            onClearQueryClicked = ::clearQuery,
            onFilterSelected = ::onFilterSelected,
        )
    )

    val viewState = _viewState.asStateFlow()

    init {
        getDocs()
    }

    private fun onFilterSelected(type: HateRateType) {
        _viewState.update {
            it.copy(
                selectedType = if (type == viewState.value.selectedType) null else type,
            )
        }
    }

    private fun getDocs() {
        viewModelScope.launch {
            viewState.flatMapLatest { state ->
                docsRepository.getAllDocsFlow(
                    query = state.query,
                    type = state.selectedType,
                ).map {
                    it.map { document ->
                        uiModelsMapper.toDocumentUi(document)
                    }
                }.onEach { docs ->
                    _viewState.update {
                        it.copy(
                            docs = docs
                        )
                    }
                }
            }.collect()
        }
    }

    private fun onSearchQueryChanged(query: String) {
        _viewState.update {
            it.copy(query = query)
        }
    }

    private fun clearQuery() {
        _viewState.update {
            it.copy(query = "")
        }
    }
}
