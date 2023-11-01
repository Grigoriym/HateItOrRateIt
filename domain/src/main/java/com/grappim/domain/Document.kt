package com.grappim.domain

import java.time.OffsetDateTime

data class Document(
    val id: Long,
    val name: String,
    val filesUri: List<ProductFileData>,
    val createdDate: OffsetDateTime,
    val documentFolderName: String,
    val description: String,
    val shop: String,
    val type: HateRateType,
)
