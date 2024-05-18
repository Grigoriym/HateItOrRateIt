package com.grappim.hateitorrateit.model

import com.grappim.hateitorrateit.domain.ProductImage

data class ProductDetailsImageUi(
    val images: List<ProductImage> = emptyList()
)
