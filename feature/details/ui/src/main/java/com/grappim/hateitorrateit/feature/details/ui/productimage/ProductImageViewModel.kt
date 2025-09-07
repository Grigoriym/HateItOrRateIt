package com.grappim.hateitorrateit.feature.details.ui.productimage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.feature.details.ui.mappers.UiModelsMapper
import com.grappim.hateitorrateit.feature.details.ui.navigation.ProductImageNavDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductImageViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val uiModelsMapper: UiModelsMapper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ProductImageNavDestination>()

    private val productId = route.productId

    private val index = route.index

    private val _viewState = MutableStateFlow(
        ImageViewModelState()
    )
    val viewState = _viewState.asStateFlow()

    init {
        getProduct()
    }

    private fun getProduct() {
        viewModelScope.launch {
            val product = productsRepository.getProductById(productId)
            val productUi = uiModelsMapper.toProductDetailsImageUI(product)
            val uri = productUi.images[index]
            _viewState.update {
                it.copy(uri = uri.uriString)
            }
        }
    }
}
