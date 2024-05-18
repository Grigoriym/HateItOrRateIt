package com.grappim.hateitorrateit.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.analyticsapi.DetailsAnalytics
import com.grappim.hateitorrateit.core.navigation.RootNavDestinations
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.model.UiModelsMapper
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
    private val detailsAnalytics: DetailsAnalytics,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId =
        requireNotNull(savedStateHandle.get<Long>(RootNavDestinations.Details.KEY))

    private val _viewState = MutableStateFlow(
        DetailsViewState(
            onDeleteProduct = ::deleteProduct,
            onShowAlertDialog = ::showAlertDialog,
            onDeleteProductConfirm = ::deleteProductConfirm,
            updateProduct = ::updateProduct,
            trackScreenStart = ::trackScreenStart,
            trackEditButtonClicked = ::trackEditClicked
        )
    )

    val viewState = _viewState.asStateFlow()

    init {
        getProduct()
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
                    productFolderName = productDetailsUi.productFolderName
                )
            }
        }
    }
}
