package com.grappim.hateitorrateit.feature.home.ui

import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.feature.home.ui.models.ProductListUI

data class HomeViewState(
    val query: String = "",

    val onSearchQueryChanged: (query: String) -> Unit = {},
    val onClearQueryClicked: () -> Unit = {},

    val products: List<ProductListUI> = emptyList(),

    val selectedType: HateRateType? = null,
    val onFilterSelected: (HateRateType) -> Unit = {},

    val trackScreenStart: () -> Unit,
    val trackOnProductClicked: () -> Unit
)
