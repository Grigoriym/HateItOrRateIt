package com.grappim.hateitorrateit.uikit.models

import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.data.repoapi.models.ProductImage

data class ProductDetailsUi(
    val id: String,
    val name: String,
    val createdDate: String,
    val images: List<com.grappim.hateitorrateit.data.repoapi.models.ProductImage>,
    val productFolderName: String,
    val description: String,
    val shop: String,
    val type: com.grappim.hateitorrateit.data.repoapi.models.HateRateType
)
