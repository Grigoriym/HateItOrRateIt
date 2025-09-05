package com.grappim.hateitorrateit.data.repoapi.models

import java.time.OffsetDateTime

data class CreateProduct(
    val id: Long,
    val name: String,
    val images: List<ProductImage>,
    val createdDate: OffsetDateTime,
    val productFolderName: String,
    val description: String,
    val shop: String,
    val type: HateRateType
) {
    companion object {
        /**
         * Creates a new CreateProduct with id = 0L (for creating new products)
         */
        fun newProduct(
            name: String,
            images: List<ProductImage>,
            createdDate: OffsetDateTime,
            productFolderName: String,
            description: String,
            shop: String,
            type: HateRateType
        ) = CreateProduct(
            id = 0L,
            name = name,
            images = images,
            createdDate = createdDate,
            productFolderName = productFolderName,
            description = description,
            shop = shop,
            type = type
        )
    }
}
