package com.grappim.hateitorrateit.ui.screens.productmanager

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.core.NativeText
import com.grappim.hateitorrateit.core.SnackbarStateViewModel
import com.grappim.hateitorrateit.core.SnackbarStateViewModelImpl
import com.grappim.hateitorrateit.core.navigation.RootNavDestinations
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.BackupImagesRepository
import com.grappim.hateitorrateit.domain.CreateProduct
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.domain.Product
import com.grappim.hateitorrateit.ui.R
import com.grappim.hateitorrateit.utils.FileUtils
import com.grappim.hateitorrateit.utils.mappers.ImageDataMapper
import com.grappim.hateitorrateit.utils.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.models.ImageData
import com.grappim.hateitorrateit.utils.productmanager.ProductImageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProductManagerViewModel @Inject constructor(
    private val fileUtils: FileUtils,
    private val productsRepository: ProductsRepository,
    private val dataCleaner: DataCleaner,
    private val localDataStorage: LocalDataStorage,
    private val backupImagesRepository: BackupImagesRepository,
    private val productImageManager: ProductImageManager,
    private val imageDataMapper: ImageDataMapper,
    savedStateHandle: SavedStateHandle,
) : ViewModel(),
    SnackbarStateViewModel by SnackbarStateViewModelImpl() {

    private val _viewState = MutableStateFlow(
        ProductManagerViewState(
            setDescription = ::setDescription,
            setName = ::setName,
            setShop = ::setShop,
            onRemoveImageClicked = ::removeImage,
            onAddImageFromGalleryClicked = ::addImageFromGallery,
            onAddCameraPictureClicked = ::addCameraPicture,
            onQuit = ::onQuit,
            onProductDone = ::onProductDone,
            getCameraImageFileUri = ::getCameraImageFileUri,
            onTypeClicked = ::onTypeClicked,
            onShowAlertDialog = ::onShowAlertDialog,
            onForceQuit = ::onForceQuit,
        )
    )
    val viewState = _viewState.asStateFlow()

    private val editProductId: String? =
        savedStateHandle[RootNavDestinations.ProductManager.KEY_EDIT_PRODUCT_ID]

    private val editProductIdLong: Long
        get() = requireNotNull(editProductId?.toLong())

    private val productFolderName: String
        get() = if (_viewState.value.isNewProduct) {
            requireNotNull(viewState.value.draftProduct).productFolderName
        } else {
            requireNotNull(_viewState.value.editProduct).productFolderName
        }

    init {
        viewModelScope.launch {
            localDataStorage.typeFlow.collect { value ->
                _viewState.update {
                    it.copy(type = value)
                }
            }
        }

        if (editProductId?.isNotEmpty() == true) {
            prepareProductForEdit()
        } else {
            prepareDraftProduct()
        }
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

    private fun prepareDraftProduct() {
        viewModelScope.launch {
            val draftProduct = productsRepository.addDraftProduct()
            _viewState.update {
                it.copy(
                    type = draftProduct.type,
                    draftProduct = draftProduct,
                    bottomBarButtonText = NativeText.Resource(R.string.create),
                    alertDialogText = NativeText.Resource(R.string.if_quit_lose_data),
                )
            }
        }
    }

    private fun prepareProductForEdit() {
        viewModelScope.launch {
            val editProduct = productsRepository.getProductById(editProductIdLong)

            productImageManager.copyToBackupFolder(
                productFolderName = editProduct.productFolderName
            )
            backupImagesRepository.insertImages(
                productId = editProductIdLong,
                images = editProduct.images,
            )

            val images = imageDataMapper.toImageDataList(editProduct.images)

            _viewState.update {
                it.copy(
                    images = images,
                    productName = editProduct.name,
                    description = editProduct.description,
                    shop = editProduct.shop,
                    type = editProduct.type,
                    isNewProduct = false,
                    bottomBarButtonText = NativeText.Resource(R.string.save),
                    alertDialogText = NativeText.Resource(R.string.if_quit_ensure_saved),
                    editProduct = editProduct,
                )
            }
        }
    }

    private fun addImageFromGallery(uri: Uri) {
        viewModelScope.launch {
            val imageData = fileUtils.getFileUriFromGalleryUri(
                uri = uri,
                folderName = productFolderName,
                isEdit = editProductId?.isNotEmpty() == true,
            )
            addImageData(imageData)
        }
    }

    private fun addCameraPicture(cameraTakePictureData: CameraTakePictureData) {
        viewModelScope.launch {
            val imageData = fileUtils.getFileDataFromCameraPicture(
                cameraTakePictureData = cameraTakePictureData,
                isEdit = editProductId?.isNotEmpty() == true,
            )
            addImageData(imageData)
        }
    }

    private fun addImageData(imageData: ImageData) {
        val result = _viewState.value.images + imageData
        _viewState.update {
            it.copy(images = result)
        }
    }

    private fun getCameraImageFileUri(): CameraTakePictureData =
        fileUtils.getFileUriForTakePicture(
            folderName = productFolderName,
            isEdit = editProductId?.isNotEmpty() == true,
        )

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

    private fun saveNewProduct() {
        viewModelScope.launch {
            val currentDraft = requireNotNull(viewState.value.draftProduct)
            val name = _viewState.value.productName.trim()
            val description = _viewState.value.description.trim()
            val shop = _viewState.value.shop.trim()
            val type = _viewState.value.type
            val images = imageDataMapper.toProductImageDataList(_viewState.value.images)

            productsRepository.addProduct(
                CreateProduct(
                    id = currentDraft.id,
                    name = name,
                    images = images,
                    createdDate = currentDraft.date,
                    productFolderName = currentDraft.productFolderName,
                    description = description,
                    shop = shop,
                    type = type,
                )
            )
            _viewState.update {
                it.copy(productSaved = true)
            }
        }
    }

    private fun editProduct() {
        viewModelScope.launch {
            val name = _viewState.value.productName.trim()
            val description = _viewState.value.description.trim()
            val shop = _viewState.value.shop.trim()
            val type = _viewState.value.type
            val editedImages = fileUtils.prepareEditedImagesToPersist(_viewState.value.images)

            val editProduct = requireNotNull(_viewState.value.editProduct)
            val product = Product(
                id = editProductIdLong,
                name = name,
                images = editedImages,
                createdDate = editProduct.createdDate,
                productFolderName = editProduct.productFolderName,
                description = description,
                shop = shop,
                type = type,
            )

            productImageManager.moveFromTempToOriginalFolder(productFolderName)
            dataCleaner.deleteTempFolder(
                productFolderName = productFolderName
            )

            dataCleaner.deleteBackupFolder(
                productFolderName = productFolderName
            )
            backupImagesRepository.deleteImagesByProductId(editProductIdLong)

            productsRepository.updateProductWithImages(
                product = product,
                images = editedImages
            )

            _viewState.update {
                it.copy(productSaved = true)
            }
        }
    }

    private fun onProductDone() {
        viewModelScope.launch {
            when {
                _viewState.value.productName.isBlank() -> {
                    setSnackbarMessageSuspend(NativeText.Resource(R.string.set_name))
                }

                _viewState.value.images.isEmpty() -> {
                    setSnackbarMessageSuspend(NativeText.Resource(R.string.add_image))
                }

                else -> {
                    if (_viewState.value.isNewProduct) {
                        saveNewProduct()
                    } else {
                        editProduct()
                    }
                }
            }
        }
    }

    private fun removeImage(imageData: ImageData) {
        viewModelScope.launch {
            if (fileUtils.deleteFile(uri = imageData.uri)) {
                Timber.d("file removed: $imageData")

                if (!_viewState.value.isNewProduct) {
                    productsRepository.deleteProductImage(
                        productId = editProductIdLong,
                        imageName = imageData.name,
                    )
                }

                _viewState.update { currentState ->
                    val updatedFilesUris = currentState.images.filterNot { it == imageData }
                    currentState.copy(images = updatedFilesUris)
                }
            }
        }
    }

    private fun onQuit() {
        viewModelScope.launch {
            _viewState.update {
                it.copy(quitStatus = QuitStatus.InProgress)
            }

            if (_viewState.value.isNewProduct) {
                val draftProduct = requireNotNull(viewState.value.draftProduct)
                dataCleaner.clearProductData(
                    productId = draftProduct.id,
                    productFolderName = productFolderName
                )
            } else {
                val initialImages = backupImagesRepository.getAllByProductId(editProductIdLong)
                productsRepository.updateImagesInProduct(
                    id = editProductIdLong,
                    images = initialImages,
                )

                productImageManager.moveFromBackupToOriginalFolder(productFolderName)

                dataCleaner.deleteTempFolder(
                    productFolderName = productFolderName
                )

                dataCleaner.deleteBackupFolder(
                    productFolderName = productFolderName
                )

                backupImagesRepository.deleteImagesByProductId(editProductIdLong)
            }

            _viewState.update {
                it.copy(quitStatus = QuitStatus.Finish)
            }
        }
    }
}