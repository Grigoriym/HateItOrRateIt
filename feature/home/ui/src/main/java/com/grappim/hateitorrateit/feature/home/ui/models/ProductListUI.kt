package com.grappim.hateitorrateit.feature.home.ui.models

import com.grappim.hateitorrateit.data.repoapi.models.HateRateType

data class ProductListUI(
    val id: String,
    val name: String,
    val shop: String,
    val createdDate: String,
    val previewUriString: String,
    val productFolderName: String,
    val type: HateRateType
)
