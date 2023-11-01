package com.grappim.hateitorrateit.model

import com.grappim.domain.ProductImageData

data class ProcuctDetailsImageUi(
    val filesUri: List<ProductImageData> = emptyList(),
)