package com.grappim.hateitorrateit.ui.screens.details.productimage

import com.grappim.hateitorrateit.domain.ProductImageData

data class ImageViewModelState(
    val uri: String = "",
    val images: List<ProductImageData> = emptyList()
)
