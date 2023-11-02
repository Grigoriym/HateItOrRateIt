package com.grappim.hateitorrateit.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grappim.hateitorrateit.domain.HateRateType
import java.time.OffsetDateTime

@Entity(
    tableName = "products_table"
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val productId: Long = 0,
    val name: String,
    val createdDate: OffsetDateTime,
    val productFolderName: String,
    val description: String,
    val shop: String,
    val type: HateRateType,
    val isCreated: Boolean = false,
)
