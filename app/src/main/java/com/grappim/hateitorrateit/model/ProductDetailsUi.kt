package com.grappim.hateitorrateit.model

import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.domain.ProductImageData

data class ProductDetailsUi(
    val id: String,
    val name: String,
    val createdDate: String,
    val filesUri: List<ProductImageData>,
    val productFolderName: String,
    val description: String,
    val shop: String,
    val type: HateRateType
)
