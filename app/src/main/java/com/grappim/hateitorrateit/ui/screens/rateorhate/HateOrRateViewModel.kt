package com.grappim.hateitorrateit.ui.screens.rateorhate

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.core.DataCleaner
import com.grappim.hateitorrateit.core.NativeText
import com.grappim.hateitorrateit.core.SnackbarStateViewModel
import com.grappim.hateitorrateit.core.SnackbarStateViewModelImpl
import com.grappim.hateitorrateit.data.ProductsRepository
import com.grappim.hateitorrateit.data.storage.local.LocalDataStorage
import com.grappim.hateitorrateit.model.CreateProduct
import com.grappim.hateitorrateit.utils.CameraTakePictureData
import com.grappim.hateitorrateit.utils.ImageData
import com.grappim.hateitorrateit.utils.FileUtils
import com.grappim.hateitorrateit.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HateOrRateViewModel @Inject constructor(
    private val fileUtils: FileUtils,
    private val productsRepository: ProductsRepository,
    private val dataCleaner: DataCleaner,
    private val localDataStorage: LocalDataStorage,
) : ViewModel(),
    SnackbarStateViewModel by SnackbarStateViewModelImpl() {

    private val _viewState = MutableStateFlow(
        HateOrRateViewState(
            setDescription = ::setDescription,
            setName = ::setName,
            setShop = ::setShop,
            onRemoveImageTriggered = ::removeFile,
            onAddImageFromGalleryClicked = ::addImageFromGallery,
            onAddCameraPictureClicked = ::addCameraPicture,
            removeData = ::removeData,
            saveData = ::saveData,
            createProduct = ::createProduct,
            getCameraImageFileUri = ::getCameraImageFileUri,
            onTypeClicked = ::onTypeClicked,
            onShowAlertDialog = ::onShowAlertDialog,
            onForceQuit = ::onForceQuit,
        )
    )
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            localDataStorage.typeFlow.collect { value ->
                _viewState.update {
                    it.copy(type = value)
                }
            }
        }

        addDraftProduct()
    }

    private fun onForceQuit() {
        _viewState.update {
            it.copy(forceQuit = true)
        }
    }

    private fun onShowAlertDialog(show: Boolean) {
        _viewState.update {
            it.copy(
                showAlertDialog = show
            )
        }
    }

    private fun onTypeClicked(newType: HateRateType) {
        if (_viewState.value.type == newType) return
        _viewState.update {
            it.copy(type = HateRateType.changeType(it.type))
        }
    }

    private fun addDraftProduct() {
        viewModelScope.launch {
            val draftProduct = productsRepository.addDraftProduct()
            _viewState.update {
                it.copy(
                    type = draftProduct.type,
                    draftProduct = draftProduct,
                )
            }
        }
    }

    private fun addImageFromGallery(uri: Uri) {
        viewModelScope.launch {
            val fileData = fileUtils.getFileUrisFromGalleryUri(
                uri = uri,
                folderName = requireNotNull(viewState.value.draftProduct).folderName
            )
            addImageData(fileData)
        }
    }

    private fun addImageData(imageData: ImageData) {
        val result = _viewState.value.images + imageData
        _viewState.update {
            it.copy(images = result)
        }
    }

    private fun addCameraPicture(cameraTakePictureData: CameraTakePictureData) {
        viewModelScope.launch {
            val fileData = fileUtils.getFileDataFromCameraPicture(
                cameraTakePictureData = cameraTakePictureData
            )
            addImageData(fileData)
        }
    }

    private fun getCameraImageFileUri(): CameraTakePictureData =
        fileUtils.getFileUriForTakePicture((requireNotNull(viewState.value.draftProduct)).folderName)

    private fun removeData() {
        viewModelScope.launch {
            val draftProduct = requireNotNull(viewState.value.draftProduct)
            dataCleaner.clearProductData(draftProduct)
        }
    }

    private fun saveData() {
        viewModelScope.launch {
            saveProduct()
        }
    }

    private fun setName(name: String) {
        _viewState.update {
            it.copy(productName = name)
        }
    }

    private fun setDescription(description: String) {
        _viewState.update {
            it.copy(description = description)
        }
    }

    private fun setShop(shop: String) {
        _viewState.update {
            it.copy(shop = shop)
        }
    }

    private fun saveProduct() {
        viewModelScope.launch {
            val currentDraft = requireNotNull(viewState.value.draftProduct)
            val name = _viewState.value.productName.trim()
            val description = _viewState.value.description.trim()
            val shop = _viewState.value.shop.trim()
            val type = _viewState.value.type

            productsRepository.addProduct(
                CreateProduct(
                    id = currentDraft.id,
                    name = name,
                    filesUri = _viewState.value.images.map {
                        fileUtils.toProductImageData(it)
                    },
                    createdDate = currentDraft.date,
                    productFolderName = currentDraft.folderName,
                    description = description,
                    shop = shop,
                    type = type,
                )
            )
            _viewState.update {
                it.copy(isCreated = true)
            }
        }
    }

    private fun createProduct() {
        viewModelScope.launch {
            when {
                _viewState.value.productName.isBlank() -> {
                    setSnackbarMessageSuspend(NativeText.Resource(R.string.set_name))
                }

                _viewState.value.images.isEmpty() -> {
                    setSnackbarMessageSuspend(NativeText.Resource(R.string.add_image))
                }

                else -> {
                    saveProduct()
                }
            }
        }
    }

    private fun removeFile(imageData: ImageData) {
        if (fileUtils.deleteFile(imageData.uri)) {
            Timber.d("file removed: $imageData")
            _viewState.update { currentState ->
                val updatedFilesUris = currentState.images.filterNot { it == imageData }
                currentState.copy(images = updatedFilesUris)
            }
        }
    }
}
