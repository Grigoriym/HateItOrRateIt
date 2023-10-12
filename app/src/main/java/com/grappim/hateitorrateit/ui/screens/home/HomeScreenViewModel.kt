package com.grappim.hateitorrateit.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.core.WhileViewSubscribed
import com.grappim.hateitorrateit.data.DocsRepository
import com.grappim.hateitorrateit.model.UiModelsMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    docsRepository: DocsRepository,
    private val uiModelsMapper: UiModelsMapper,
) : ViewModel() {

    val docs = docsRepository.getAllDocsFlow()
        .map { list ->
            list.map { document ->
                uiModelsMapper.toDocumentUi(document)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = WhileViewSubscribed,
            initialValue = emptyList(),
        )
}
