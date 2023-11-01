package com.grappim.hateitorrateit.ui.screens.details

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.domain.HateRateType
import com.grappim.hateitorrateit.core.DataCleaner
import com.grappim.hateitorrateit.core.navigation.RootNavDestinations
import com.grappim.hateitorrateit.data.DocsRepository
import com.grappim.hateitorrateit.model.UiModelsMapper
import com.grappim.hateitorrateit.utils.CameraTakePictureData
import com.grappim.hateitorrateit.utils.FileData
import com.grappim.hateitorrateit.utils.FileUtils
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
    private val dataCleaner: DataCleaner,
    private val fileUtils: FileUtils,
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
            onDeleteDocument = ::onDeleteDocument,
            onShowAlertDialog = ::onShowAlertDialog,
            onDeleteProductConfirm = ::onDeleteConfirm,
            onDeleteImage = ::onDeleteImage,
            onAddImageFromGalleryClicked = ::addImageFromGallery,
            onAddCameraPictureClicked = ::addCameraPicture,
            getCameraImageFileUri = ::getCameraImageFileUri,
        )
    )

    val viewState = _viewState.asStateFlow()

    init {
        getDoc(docId)
    }

    private fun addCameraPicture(cameraTakePictureData: CameraTakePictureData) {
        viewModelScope.launch {
            val fileData = fileUtils.getFileDataFromCameraPicture(
                cameraTakePictureData = cameraTakePictureData
            )
            addFileData(fileData)
        }
    }

    private fun addImageFromGallery(uri: Uri) {
        viewModelScope.launch {
            val fileData = fileUtils.getFileUrisFromGalleryUri(
                uri = uri,
                folderName = viewState.value.documentFolderName,
            )
            addFileData(fileData)
        }
    }

    private fun addFileData(fileData: FileData) {
        viewModelScope.launch {
            val productFileData = fileUtils.toDocumentFileData(fileData)
            docsRepository.updateImagesInProduct(
                id = viewState.value.id.toLong(),
                files = listOf(productFileData)
            )

            val result = _viewState.value.filesUris + productFileData
            _viewState.update {
                it.copy(filesUris = result)
            }
        }
    }

    private fun getCameraImageFileUri(): CameraTakePictureData =
        fileUtils.getFileUriForTakePicture(viewState.value.documentFolderName)

    private fun onDeleteImage(pageIndex: Int) {
        viewModelScope.launch {
            val fileData = viewState.value.filesUris[pageIndex]
            val name = fileData.name
            val result = dataCleaner.clearProductImage(
                id = viewState.value.id.toLong(),
                imageName = name,
                uriString = fileData.uriString
            )

            if (result) {
                _viewState.update { currentState ->
                    val updatedFilesUris = currentState.filesUris.filterNot { it.name == name }
                    currentState.copy(filesUris = updatedFilesUris)
                }
            }
        }
    }

    private fun onDeleteConfirm() {
        viewModelScope.launch {
            _viewState.update {
                it.copy(isLoading = true)
            }

            val id = viewState.value.id.toLong()
            val folderName = viewState.value.documentFolderName
            dataCleaner.clearDocumentData(id, folderName)

            _viewState.update {
                it.copy(productDeleted = true)
            }
        }
    }

    private fun onShowAlertDialog(show: Boolean) {
        _viewState.update {
            it.copy(
                showAlertDialog = show
            )
        }
    }

    private fun onDeleteDocument() {
        _viewState.update {
            it.copy(showAlertDialog = true)
        }
    }

    private fun toggleEditMode() {
        _viewState.update {
            it.copy(
                nameToEdit = viewState.value.name,
                descriptionToEdit = viewState.value.description,
                shopToEdit = viewState.value.shop,
                typeToEdit = viewState.value.type,
                isEdit = !viewState.value.isEdit,
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
                type = requireNotNull(viewState.value.typeToEdit),
            )
        }
        _viewState.update {
            it.copy(
                name = viewState.value.nameToEdit,
                description = viewState.value.descriptionToEdit,
                shop = viewState.value.shopToEdit,
                type = viewState.value.typeToEdit,
                isEdit = !viewState.value.isEdit,
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
            it.copy(typeToEdit = HateRateType.changeType(requireNotNull(it.typeToEdit)))
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
                    filesUris = docUi.filesUri,
                    isLoading = false,
                    type = docUi.type,
                    documentFolderName = docUi.documentFolderName,
                )
            }
        }
    }
}
