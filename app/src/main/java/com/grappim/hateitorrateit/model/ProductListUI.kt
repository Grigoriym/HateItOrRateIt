package com.grappim.hateitorrateit.model

import com.grappim.domain.HateRateType

data class ProductListUI(
    val id: String,
    val name: String,
    val shop: String,
    val createdDate: String,
    val previewUriString: String,
    val productFolderName: String,
    val type: HateRateType,
)
