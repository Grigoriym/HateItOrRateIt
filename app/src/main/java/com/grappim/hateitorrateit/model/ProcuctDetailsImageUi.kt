package com.grappim.hateitorrateit.model

import com.grappim.hateitorrateit.domain.ProductImageData

data class ProcuctDetailsImageUi(
    val filesUri: List<ProductImageData> = emptyList(),
)