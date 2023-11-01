package com.grappim.hateitorrateit.model

import com.grappim.domain.ProductImageData
import com.grappim.domain.HateRateType

data class ProductDetailsUi(
    val id: String,
    val name: String,
    val createdDate: String,
    val filesUri: List<ProductImageData>,
    val productFolderName: String,
    val description: String,
    val shop: String,
    val type: HateRateType,
)
