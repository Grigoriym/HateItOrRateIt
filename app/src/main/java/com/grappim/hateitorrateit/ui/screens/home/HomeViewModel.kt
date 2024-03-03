package com.grappim.hateitorrateit.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.analyticsapi.HomeAnalytics
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.model.UiModelsMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val uiModelsMapper: UiModelsMapper,
    private val homeAnalytics: HomeAnalytics
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        HomeViewState(
            onSearchQueryChanged = ::onSearchQueryChanged,
            onClearQueryClicked = ::clearQuery,
            onFilterSelected = ::onFilterSelected,
            trackScreenStart = ::trackScreenStart,
            trackOnProductClicked = ::trackOnProductClicked
        )
    )

    val viewState = _viewState.asStateFlow()

    init {
        getProducts()
    }

    private fun trackOnProductClicked() {
        homeAnalytics.trackProductClicked()
    }

    private fun trackScreenStart() {
        homeAnalytics.trackHomeScreenStart()
    }

    private fun onFilterSelected(type: HateRateType) {
        _viewState.update {
            it.copy(
                selectedType = if (type == viewState.value.selectedType) null else type
            )
        }
    }

    private fun getProducts() {
        viewModelScope.launch {
            viewState.flatMapLatest { state ->
                productsRepository.getProductsFlow(
                    query = state.query,
                    type = state.selectedType
                ).map {
                    it.map { product ->
                        uiModelsMapper.toProductUi(product)
                    }
                }.onEach { products ->
                    _viewState.update {
                        it.copy(
                            products = products
                        )
                    }
                }
            }.collect()
        }
    }

    private fun onSearchQueryChanged(query: String) {
        _viewState.update {
            it.copy(query = query)
        }
    }

    private fun clearQuery() {
        _viewState.update {
            it.copy(query = "")
        }
    }
}
