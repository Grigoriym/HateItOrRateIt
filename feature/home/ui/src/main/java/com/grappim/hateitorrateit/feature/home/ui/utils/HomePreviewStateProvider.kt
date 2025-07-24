package com.grappim.hateitorrateit.feature.home.ui.utils

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.feature.home.ui.HomeViewState
import com.grappim.hateitorrateit.feature.home.ui.models.ProductListUI
import kotlinx.collections.immutable.persistentListOf

internal class HomePreviewStateProvider : PreviewParameterProvider<HomeViewState> {
    override val values: Sequence<HomeViewState>
        get() = sequenceOf(
            HomeViewState(
                query = "namee",
                onSearchQueryChanged = {},
                onClearQueryClicked = {},
                products = persistentListOf(),
                selectedType = HateRateType.HATE,
                onFilterSelected = {},
                trackScreenStart = {},
                trackOnProductClicked = {}
            )
        )
}

internal fun getPreviewProductListUI() = ProductListUI(
    id = "inciderint",
    name = "Claude Best",
    shop = "posse",
    createdDate = "ferri",
    previewUriString = "regione",
    productFolderName = "Rosie McDaniel",
    type = HateRateType.RATE
)
