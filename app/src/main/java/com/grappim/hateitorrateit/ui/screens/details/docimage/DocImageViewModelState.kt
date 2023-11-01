package com.grappim.hateitorrateit.ui.screens.details.docimage

import com.grappim.domain.ProductFileData

data class DocImageViewModelState(
    val uri: String = "",
    val fileUris: List<ProductFileData> = emptyList(),
)
