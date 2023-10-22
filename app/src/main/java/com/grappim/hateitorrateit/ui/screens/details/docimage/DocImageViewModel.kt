package com.grappim.hateitorrateit.ui.screens.details.docimage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.core.di.IoDispatcher
import com.grappim.hateitorrateit.core.navigation.RootNavDestinations
import com.grappim.hateitorrateit.data.DocsRepository
import com.grappim.hateitorrateit.model.UiModelsMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocImageViewModel @Inject constructor(
    private val docsRepository: DocsRepository,
    private val uiModelsMapper: UiModelsMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val docId = checkNotNull(
        savedStateHandle.get<String>(RootNavDestinations.DetailsImage.KEY_DOC_ID)
    )

    private val index = checkNotNull(
        savedStateHandle.get<Int>(RootNavDestinations.DetailsImage.KEY_INDEX)
    )

    private val _viewState = MutableStateFlow(
        DocImageViewModelState()
    )
    val viewState = _viewState.asStateFlow()

    init {
        getDoc()
    }

    private fun getDoc() {
        viewModelScope.launch {
            val document = docsRepository.getDocById(docId.toLong())
            val docUi = uiModelsMapper.toDocumentDetailsImageU(document)
            val uri = docUi.filesUri[index]
            _viewState.update {
                it.copy(
                    uri = uri.uriString,
                    fileUris = docUi.filesUri
                )
            }
        }
    }
}
