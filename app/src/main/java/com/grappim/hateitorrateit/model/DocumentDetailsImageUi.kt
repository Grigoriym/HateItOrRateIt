package com.grappim.hateitorrateit.model

import com.grappim.domain.ProductFileData

data class DocumentDetailsImageUi(
    val filesUri: List<ProductFileData> = emptyList(),
)