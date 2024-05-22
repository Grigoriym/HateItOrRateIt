package com.grappim.hateitorrateit.feature.details.ui.productimage

import com.grappim.hateitorrateit.data.repoapi.models.ProductImage

data class ImageViewModelState(
    val uri: String = "",
    val images: List<ProductImage> = emptyList()
)
