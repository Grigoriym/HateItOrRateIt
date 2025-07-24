@file:OptIn(ExperimentalCoroutinesApi::class)

package com.grappim.hateitorrateit.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.data.analyticsapi.HomeAnalytics
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.feature.home.ui.utils.HomeUIModelsMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val homeUIModelsMapper: HomeUIModelsMapper,
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
                        homeUIModelsMapper.toProductUi(product)
                    }.toPersistentList()
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
