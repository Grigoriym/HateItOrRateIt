package com.grappim.hateitorrateit.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.domain.HateRateType
import com.grappim.hateitorrateit.core.navigation.RootNavDestinations
import com.grappim.hateitorrateit.data.DocsRepository
import com.grappim.hateitorrateit.model.UiModelsMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val docsRepository: DocsRepository,
    private val uiModelsMapper: UiModelsMapper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val docId = checkNotNull(savedStateHandle.get<Long>(RootNavDestinations.Details.KEY))

    private val _viewState = MutableStateFlow(
        DetailsViewState(
            onSaveName = ::setName,
            onSaveDescription = ::setDescription,
            onSaveShop = ::onSaveShop,
            toggleEditMode = ::toggleEditMode,
            onEditSubmit = ::onEditSubmit,
            onTypeChanged = ::onTypeChanged,
        )
    )

    val viewState = _viewState.asStateFlow()

    init {
        getDoc(docId)
    }

    private fun toggleEditMode() {
        _viewState.update {
            it.copy(isEdit = !_viewState.value.isEdit)
        }
    }

    private fun onTypeChanged(newType: HateRateType) {
        if (_viewState.value.type == newType) return
        _viewState.update {
            it.copy(type = HateRateType.changeType(requireNotNull(it.type)))
        }
    }

    private fun onEditSubmit() {
        toggleEditMode()
        viewModelScope.launch {
            docsRepository.updateDoc(
                id = viewState.value.id.toLong(),
                name = viewState.value.name,
                description = viewState.value.description,
                shop = viewState.value.shop,
                type = requireNotNull(viewState.value.type)
            )
        }
    }

    private fun setName(name: String) {
        _viewState.update {
            it.copy(name = name)
        }
    }

    private fun setDescription(description: String) {
        _viewState.update {
            it.copy(description = description)
        }
    }

    private fun onSaveShop(shop: String) {
        _viewState.update {
            it.copy(shop = shop)
        }
    }

    private fun getDoc(id: Long) {
        viewModelScope.launch {
            val document = docsRepository.getDocById(id)
            val docUi = uiModelsMapper.toDocumentDetailsUi(document)
            _viewState.update {
                it.copy(
                    id = docUi.id,
                    name = docUi.name,
                    description = docUi.description,
                    shop = docUi.shop,
                    createdDate = docUi.createdDate,
                    filesUri = docUi.filesUri,
                    isLoading = false,
                    type = docUi.type,
                )
            }
        }
    }
}
