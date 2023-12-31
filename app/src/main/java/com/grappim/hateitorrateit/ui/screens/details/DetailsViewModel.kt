package com.grappim.hateitorrateit.ui.screens.details

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.core.navigation.RootNavDestinations
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.model.UiModelsMapper
import com.grappim.hateitorrateit.utils.FileUtils
import com.grappim.hateitorrateit.utils.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.models.ImageData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val uiModelsMapper: UiModelsMapper,
    private val dataCleaner: DataCleaner,
    private val fileUtils: FileUtils,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId =
        checkNotNull(savedStateHandle.get<Long>(RootNavDestinations.Details.KEY))

    private val _viewState = MutableStateFlow(
        DetailsViewState(
            onSetName = ::setName,
            onSetDescription = ::setDescription,
            onSetShop = ::setShop,
            onToggleEditMode = ::toggleEditMode,
            onSubmitChanges = ::submitChanges,
            onSetType = ::setType,
            onDeleteProduct = ::deleteProduct,
            onShowAlertDialog = ::showAlertDialog,
            onDeleteProductConfirm = ::deleteProductConfirm,
            onDeleteImage = ::deleteImage,
            onAddImageFromGalleryClicked = ::addImageFromGallery,
            onAddCameraPictureClicked = ::addCameraPicture,
            getCameraImageFileUri = ::getCameraImageFileUri,
        )
    )

    val viewState = _viewState.asStateFlow()

    init {
        getProduct(productId)
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
                folderName = viewState.value.productFolderName,
            )
            addFileData(fileData)
        }
    }

    private fun addFileData(imageData: ImageData) {
        viewModelScope.launch {
            val productFileData = fileUtils.toProductImageData(imageData)
            productsRepository.updateImagesInProduct(
                id = viewState.value.id.toLong(),
                files = listOf(productFileData)
            )

            val result = _viewState.value.images + productFileData
            _viewState.update {
                it.copy(images = result)
            }
        }
    }

    private fun getCameraImageFileUri(): CameraTakePictureData =
        fileUtils.getFileUriForTakePicture(viewState.value.productFolderName)

    private fun deleteImage(pageIndex: Int) {
        viewModelScope.launch {
            val fileData = viewState.value.images[pageIndex]
            val name = fileData.name
            val result = dataCleaner.clearProductImage(
                id = viewState.value.id.toLong(),
                imageName = name,
                uriString = fileData.uriString
            )

            if (result) {
                _viewState.update { currentState ->
                    val updatedFilesUris = currentState.images.filterNot { it.name == name }
                    currentState.copy(images = updatedFilesUris)
                }
            }
        }
    }

    private fun deleteProductConfirm() {
        viewModelScope.launch {
            _viewState.update {
                it.copy(isLoading = true)
            }

            val id = viewState.value.id.toLong()
            val folderName = viewState.value.productFolderName
            dataCleaner.clearProductData(id, folderName)

            _viewState.update {
                it.copy(productDeleted = true)
            }
        }
    }

    private fun showAlertDialog(show: Boolean) {
        _viewState.update {
            it.copy(
                showAlertDialog = show
            )
        }
    }

    private fun deleteProduct() {
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

    private fun submitChanges() {
        viewModelScope.launch {
            productsRepository.updateProduct(
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

    private fun getProduct(id: Long) {
        viewModelScope.launch {
            val product = productsRepository.getProductById(id)
            val productDetailsUi = uiModelsMapper.toProductDetailsUi(product)
            _viewState.update {
                it.copy(
                    id = productDetailsUi.id,
                    name = productDetailsUi.name,
                    description = productDetailsUi.description,
                    shop = productDetailsUi.shop,
                    createdDate = productDetailsUi.createdDate,
                    images = productDetailsUi.filesUri,
                    isLoading = false,
                    type = productDetailsUi.type,
                    productFolderName = productDetailsUi.productFolderName,
                )
            }
        }
    }
}
