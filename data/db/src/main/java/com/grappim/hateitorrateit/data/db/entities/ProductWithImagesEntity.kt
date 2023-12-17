package com.grappim.hateitorrateit.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * A class that represents a one-to-many relationship between a [ProductEntity] and a [ProductImageDataEntity].
 */
data class ProductWithImagesEntity(
    @Embedded val productEntity: ProductEntity,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productId"
    )
    val files: List<ProductImageDataEntity>?
)
