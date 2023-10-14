package com.grappim.hateitorrateit.model

import com.grappim.hateitorrateit.core.HateRateType
import com.grappim.hateitorrateit.domain.DocumentFileData

data class DocumentDetailsUi(
    val id: String,
    val name: String,
    val createdDate: String,
    val filesUri: List<DocumentFileData>,
    val documentFolderName: String,
    val description: String,
    val shop: String,
    val type: HateRateType,
)
