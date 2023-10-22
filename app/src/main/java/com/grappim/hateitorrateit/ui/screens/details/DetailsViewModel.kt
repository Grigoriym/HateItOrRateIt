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
            onSaveShop = ::setShop,
            toggleEditMode = ::toggleEditMode,
            onEditSubmit = ::onEditSubmit,
            onTypeChanged = ::setType,
        )
    )

    val viewState = _viewState.asStateFlow()

    init {
        getDoc(docId)
    }

    private fun toggleEditMode() {
        _viewState.update {
            it.copy(
                nameToEdit = viewState.value.name,
                descriptionToEdit = viewState.value.description,
                shopToEdit = viewState.value.shop,
                typeToEdit = viewState.value.type,
                isEdit = !viewState.value.isEdit
            )
        }
    }

    private fun onEditSubmit() {
        viewModelScope.launch {
            docsRepository.updateDoc(
                id = viewState.value.id.toLong(),
                name = viewState.value.nameToEdit,
                description = viewState.value.descriptionToEdit,
                shop = viewState.value.shopToEdit,
                type = requireNotNull(viewState.value.typeToEdit)
            )
        }
        _viewState.update {
            it.copy(
                name = viewState.value.nameToEdit,
                description = viewState.value.descriptionToEdit,
                shop = viewState.value.shopToEdit,
                type = viewState.value.typeToEdit,
                isEdit = !viewState.value.isEdit
            )
        }
    }

    private fun setName(name: String) {
        _viewState.update {
            it.copy(nameToEdit = name)
        }
    }

    private fun setDescription(description: String) {
        _viewState.update {
            it.copy(descriptionToEdit = description)
        }
    }

    private fun setShop(shop: String) {
        _viewState.update {
            it.copy(shopToEdit = shop)
        }
    }

    private fun setType(newType: HateRateType) {
        if (_viewState.value.typeToEdit == newType) return
        _viewState.update {
            it.copy(typeToEdit = HateRateType.changeType(requireNotNull(it.type)))
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
