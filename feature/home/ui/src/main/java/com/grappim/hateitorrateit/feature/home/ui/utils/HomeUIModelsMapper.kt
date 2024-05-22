package com.grappim.hateitorrateit.feature.home.ui.utils

import com.grappim.hateitorrateit.data.repoapi.models.Product
import com.grappim.hateitorrateit.feature.home.ui.models.ProductListUI

interface HomeUIModelsMapper {
    suspend fun toProductUi(product: Product): ProductListUI
}
