package com.grappim.hateitorrateit.ui.screens.home

import com.grappim.domain.HateRateType
import com.grappim.hateitorrateit.model.ProductListUI

data class HomeViewState(
    val query: String = "",

    val onSearchQueryChanged: (query: String) -> Unit,
    val onClearQueryClicked: () -> Unit,

    val products: List<ProductListUI> = emptyList(),

    val selectedType: HateRateType? = null,
    val onFilterSelected: (HateRateType) -> Unit,
)