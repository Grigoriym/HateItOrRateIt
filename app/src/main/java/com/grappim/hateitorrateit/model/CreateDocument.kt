package com.grappim.hateitorrateit.model

import com.grappim.domain.ProductFileData
import com.grappim.domain.HateRateType
import java.time.OffsetDateTime

data class CreateDocument(
    val id: Long,
    val name: String,
    val filesUri: List<ProductFileData>,
    val createdDate: OffsetDateTime,
    val documentFolderName: String,
    val description: String,
    val shop: String,
    val type: HateRateType,
)
