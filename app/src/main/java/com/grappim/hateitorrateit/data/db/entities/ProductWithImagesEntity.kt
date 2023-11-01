package com.grappim.hateitorrateit.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ProductWithImagesEntity(
    @Embedded val productEntity: ProductEntity,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productId"
    )
    val files: List<ProductImageDataEntity>?
)
