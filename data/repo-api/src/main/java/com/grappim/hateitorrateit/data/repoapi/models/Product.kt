package com.grappim.hateitorrateit.data.repoapi.models

import java.time.OffsetDateTime

data class Product(
    val id: Long,
    val name: String,
    val images: List<ProductImage>,
    val createdDate: OffsetDateTime,
    val productFolderName: String,
    val description: String,
    val shop: String,
    val type: HateRateType
)
