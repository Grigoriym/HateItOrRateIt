package com.grappim.hateitorrateit.model

import com.grappim.hateitorrateit.domain.ProductImageData

data class ProductDetailsImageUi(
    val images: List<ProductImageData> = emptyList()
)
