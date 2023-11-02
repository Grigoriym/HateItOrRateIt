package com.grappim.hateitorrateit.model

import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.domain.HateRateType
import java.time.OffsetDateTime

data class CreateProduct(
    val id: Long,
    val name: String,
    val filesUri: List<ProductImageData>,
    val createdDate: OffsetDateTime,
    val productFolderName: String,
    val description: String,
    val shop: String,
    val type: HateRateType,
)
