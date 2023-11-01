package com.grappim.domain

import java.time.OffsetDateTime

data class Product(
    val id: Long,
    val name: String,
    val filesUri: List<ProductImageData>,
    val createdDate: OffsetDateTime,
    val productFolderName: String,
    val description: String,
    val shop: String,
    val type: HateRateType,
)
