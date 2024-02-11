package com.grappim.hateitorrateit.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "product_image_data_table"
)
data class ProductImageDataEntity(
    @PrimaryKey(autoGenerate = true)
    val imageId: Long = 0,

    val productId: Long,

    val name: String,
    val mimeType: String,
    val size: Long,

    val uriPath: String,
    val uriString: String,

    val md5: String
)
