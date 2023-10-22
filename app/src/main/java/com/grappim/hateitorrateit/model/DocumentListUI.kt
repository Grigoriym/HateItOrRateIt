package com.grappim.hateitorrateit.model

import com.grappim.domain.HateRateType

data class DocumentListUI(
    val id: String,
    val name: String,
    val shop: String,
    val createdDate: String,
    val preview: Any,
    val documentFolderName: String,
    val type: HateRateType,
)
