package com.grappim.hateitorrateit.ui.screens.details.productimage

import com.grappim.hateitorrateit.domain.ProductImage

data class ImageViewModelState(
    val uri: String = "",
    val images: List<ProductImage> = emptyList()
)
