package com.grappim.hateitorrateit.feature.details.ui.model

import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.data.repoapi.models.ProductImage

data class ProductDetailsUi(
    val id: Long,
    val name: String,
    val createdDate: String,
    val images: List<ProductImage>,
    val productFolderName: String,
    val description: String,
    val shop: String,
    val type: HateRateType
)
