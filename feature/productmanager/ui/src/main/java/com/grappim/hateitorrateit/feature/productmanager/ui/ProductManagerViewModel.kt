package com.grappim.hateitorrateit.feature.productmanager.ui

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.analyticsapi.ProductManagerAnalytics
import com.grappim.hateitorrateit.core.navigation.RootNavDestinations
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.BackupImagesRepository
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.CreateProduct
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.data.repoapi.models.Product
import com.grappim.hateitorrateit.uikit.R
import com.grappim.hateitorrateit.utils.filesapi.deletion.FileDeletionUtils
import com.grappim.hateitorrateit.utils.filesapi.images.ImagePersistenceManager
import com.grappim.hateitorrateit.utils.filesapi.mappers.ImageDataMapper
import com.grappim.hateitorrateit.utils.filesapi.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.filesapi.models.ProductImageUIData
import com.grappim.hateitorrateit.utils.filesapi.productmanager.ProductImageManager
import com.grappim.hateitorrateit.utils.filesapi.urimanager.FileUriManager
import com.grappim.hateitorrateit.utils.ui.NativeText
import com.grappim.hateitorrateit.utils.ui.SnackbarStateViewModel
import com.grappim.hateitorrateit.utils.ui.SnackbarStateViewModelImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProductManagerViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val dataCleaner: DataCleaner,
    private val localDataStorage: LocalDataStorage,
    private val backupImagesRepository: BackupImagesRepository,
    private val productImageManager: ProductImageManager,
    private val imageDataMapper: ImageDataMapper,
    private val productManagerAnalytics: ProductManagerAnalytics,
    private val fileDeletionUtils: FileDeletionUtils,
    private val fileUriManager: FileUriManager,
    private val imagePersistenceManager: ImagePersistenceManager,
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    SnackbarStateViewModel by SnackbarStateViewModelImpl() {

    private val _viewState = MutableStateFlow(
        ProductManagerViewState(
            setDescription = ::setDescription,
            setName = ::setName,
            setShop = ::setShop,
            onDeleteImageClicked = ::deleteImage,
            onAddImageFromGalleryClicked = ::addImageFromGallery,
            onAddCameraPictureClicked = ::addCameraPicture,
            onQuit = ::onQuit,
            onProductDone = ::onProductDone,
            getCameraImageFileUri = ::getCameraImageFileUri,
            onTypeClicked = ::onTypeClicked,
            onShowAlertDialog = ::onShowAlertDialog,
            onForceQuit = ::onForceQuit,
            trackOnScreenStart = ::trackOnScreenStart
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

    private fun trackOnScreenStart() {
        if (editProductId?.isNotEmpty() == true) {
            productManagerAnalytics.trackProductManagerProductToEditStart()
        } else {
            productManagerAnalytics.trackProductManagerNewProductStart()
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
                    alertDialogText = NativeText.Resource(R.string.if_quit_lose_data)
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
                images = editProduct.images
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
                    editProduct = editProduct
                )
            }
        }
    }

    private fun addImageFromGallery(uri: Uri) {
        productManagerAnalytics.trackGalleryButtonClicked()
        viewModelScope.launch {
            val imageData = fileUriManager.getFileUriFromGalleryUri(
                uri = uri,
                folderName = productFolderName,
                isEdit = editProductId?.isNotEmpty() == true
            )
            addImageData(imageData)
        }
    }

    private fun addCameraPicture(cameraTakePictureData: CameraTakePictureData) {
        productManagerAnalytics.trackCameraButtonClicked()
        viewModelScope.launch {
            val imageData = fileUriManager.getFileDataFromCameraPicture(
                cameraTakePictureData = cameraTakePictureData,
                isEdit = editProductId?.isNotEmpty() == true
            )
            addImageData(imageData)
        }
    }

    private fun addImageData(productImageUIData: ProductImageUIData) {
        val result = _viewState.value.images + productImageUIData
        _viewState.update {
            it.copy(images = result)
        }
    }

    private fun getCameraImageFileUri(): CameraTakePictureData =
        fileUriManager.getFileUriForTakePicture(
            folderName = productFolderName,
            isEdit = editProductId?.isNotEmpty() == true
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
                    type = type
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
            val editedImages =
                imagePersistenceManager.prepareEditedImagesToPersist(_viewState.value.images)

            val editProduct = requireNotNull(_viewState.value.editProduct)
            val product = Product(
                id = editProductIdLong,
                name = name,
                images = editedImages,
                createdDate = editProduct.createdDate,
                productFolderName = editProduct.productFolderName,
                description = description,
                shop = shop,
                type = type
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
                        productManagerAnalytics.trackCreateButtonClicked()
                        saveNewProduct()
                    } else {
                        productManagerAnalytics.trackSaveButtonClicked()
                        editProduct()
                    }
                }
            }
        }
    }

    private fun deleteImage(productImageUIData: ProductImageUIData) {
        productManagerAnalytics.trackDeleteImageClicked()
        viewModelScope.launch {
            if (fileDeletionUtils.deleteFile(uri = productImageUIData.uri)) {
                Timber.d("file removed: $productImageUIData")

                if (!_viewState.value.isNewProduct) {
                    productsRepository.deleteProductImage(
                        productId = editProductIdLong,
                        imageName = productImageUIData.name
                    )
                }

                _viewState.update { currentState ->
                    val updatedFilesUris =
                        currentState.images.filterNot { it == productImageUIData }
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
                dataCleaner.deleteProductData(
                    productId = draftProduct.id,
                    productFolderName = productFolderName
                )
            } else {
                val initialImages = backupImagesRepository.getAllByProductId(editProductIdLong)
                productsRepository.updateImagesInProduct(
                    id = editProductIdLong,
                    images = initialImages
                )
                productImageManager.moveFromBackupToOriginalFolder(productFolderName)

                dataCleaner.deleteTempFolder(productFolderName)
                dataCleaner.deleteBackupFolder(productFolderName)

                backupImagesRepository.deleteImagesByProductId(editProductIdLong)
            }

            _viewState.update {
                it.copy(quitStatus = QuitStatus.Finish)
            }
        }
    }
}
