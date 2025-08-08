package com.grappim.hateitorrateit.feature.details.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.core.navigation.NavDestinations
import com.grappim.hateitorrateit.data.analyticsapi.DetailsAnalytics
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.ProductImage
import com.grappim.hateitorrateit.feature.details.ui.mappers.UiModelsMapper
import com.grappim.hateitorrateit.utils.androidapi.GalleryInteractions
import com.grappim.hateitorrateit.utils.androidapi.IntentGenerator
import com.grappim.hateitorrateit.utils.ui.NativeText
import com.grappim.hateitorrateit.utils.ui.SnackbarStateViewModel
import com.grappim.hateitorrateit.utils.ui.SnackbarStateViewModelImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val uiModelsMapper: UiModelsMapper,
    private val dataCleaner: DataCleaner,
    private val detailsAnalytics: DetailsAnalytics,
    private val galleryInteractions: GalleryInteractions,
    private val intentGenerator: IntentGenerator,
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    SnackbarStateViewModel by SnackbarStateViewModelImpl() {

    private val productId =
        requireNotNull(savedStateHandle.get<Long>(NavDestinations.Details.KEY))

    private val _viewEvents = Channel<DetailsEvents>()
    val viewEvents = _viewEvents.receiveAsFlow()

    private val _viewState = MutableStateFlow(
        DetailsViewState(
            appSettingsIntent = intentGenerator.generateAppSettingsIntent(),
            onDeleteProduct = ::deleteProduct,
            onShowAlertDialog = ::showAlertDialog,
            onDeleteProductConfirm = ::deleteProductConfirm,
            updateProduct = ::updateProduct,
            trackScreenStart = ::trackScreenStart,
            trackEditButtonClicked = ::trackEditClicked,
            setCurrentDisplayedImageIndex = ::setCurrentDisplayedImageIndex,
            setSnackbarMessage = ::setSnackbarMessage,
            saveFileToGallery = ::saveFileToGallery,
            onShareImageClick = ::onShareImageClicked,
            clearShareImageIntent = ::clearShareImageIntent,
            onShowPermissionsAlertDialog = ::onShowPermissionsAlertDialog
        )
    )

    val viewState = _viewState.asStateFlow()

    init {
        getProduct()
    }

    private fun onShowPermissionsAlertDialog(show: Boolean, text: String?) {
        _viewState.update {
            it.copy(
                showProvidePermissionsAlertDialog = show,
                permissionsAlertDialogText = text ?: ""
            )
        }
    }

    /**
     * To fix the issue when we navigate back to this screen and intent is called again
     */
    private fun clearShareImageIntent() {
        _viewState.update {
            it.copy(shareImageIntent = null)
        }
    }

    private fun onShareImageClicked(productImage: ProductImage) {
        val intent = intentGenerator.generateIntentToShareImage(
            uriString = productImage.uriString,
            mimeType = productImage.mimeType
        )
        _viewState.update {
            it.copy(shareImageIntent = intent)
        }
    }

    private fun saveFileToGallery(productImage: ProductImage) {
        viewModelScope.launch {
            galleryInteractions.saveImageInGallery(
                uriString = productImage.uriString,
                name = productImage.name,
                mimeType = productImage.mimeType,
                folderName = requireNotNull(viewState.value.productFolderName)
            ).onSuccess { result ->
                _viewEvents.send(DetailsEvents.SaveImageSuccess)
            }.onFailure { error ->
                Timber.e(error)
                _viewEvents.send(DetailsEvents.SaveImageFailure)
            }
        }
    }

    private fun trackScreenStart() {
        detailsAnalytics.trackDetailsScreenStart()
    }

    private fun trackDeleteProduct() {
        detailsAnalytics.trackDetailsDeleteProductButtonClicked()
    }

    private fun trackEditClicked() {
        detailsAnalytics.trackDetailsEditButtonClicked()
    }

    private fun updateProduct() {
        _viewState.update {
            it.copy(isLoading = true)
        }
        getProduct()
    }

    private fun deleteProductConfirm() {
        detailsAnalytics.trackDetailsDeleteProductConfirmed()
        viewModelScope.launch {
            _viewState.update {
                it.copy(isLoading = true)
            }

            dataCleaner.deleteProductData(
                productId = productId,
                productFolderName = viewState.value.productFolderName
            )

            _viewState.update {
                it.copy(
                    productDeleted = true
                )
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
        trackDeleteProduct()
        _viewState.update {
            it.copy(showAlertDialog = true)
        }
    }

    private fun setCurrentDisplayedImageIndex(index: Int) {
        _viewState.update {
            it.copy(currentImage = _viewState.value.images.getOrNull(index))
        }
    }

    private fun setSnackbarMessage(text: NativeText) {
        viewModelScope.launch {
            showSnackbarSuspend(text)
        }
    }

    private fun getProduct() {
        viewModelScope.launch {
            val product = productsRepository.getProductById(productId)
            val productDetailsUi = uiModelsMapper.toProductDetailsUi(product)
            _viewState.update {
                it.copy(
                    productId = productDetailsUi.id,
                    name = productDetailsUi.name,
                    description = productDetailsUi.description,
                    shop = productDetailsUi.shop,
                    createdDate = productDetailsUi.createdDate,
                    images = productDetailsUi.images,
                    isLoading = false,
                    type = productDetailsUi.type,
                    productFolderName = productDetailsUi.productFolderName,
                    currentImage = productDetailsUi.images.firstOrNull()
                )
            }
        }
    }
}
