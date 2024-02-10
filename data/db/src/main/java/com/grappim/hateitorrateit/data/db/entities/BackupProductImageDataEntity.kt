package com.grappim.hateitorrateit.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "backup_product_image_data_entity"
)
data class BackupProductImageDataEntity(
    @PrimaryKey
    val imageId: Long,

    val productId: Long,

    val name: String,
    val mimeType: String,
    val size: Long,

    val uriPath: String,
    val uriString: String,

    val md5: String
)
