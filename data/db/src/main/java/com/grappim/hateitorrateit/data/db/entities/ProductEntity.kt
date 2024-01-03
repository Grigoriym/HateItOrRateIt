package com.grappim.hateitorrateit.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grappim.hateitorrateit.domain.HateRateType
import java.time.OffsetDateTime

const val productsTable = "products_table"

/**
 * @param productId - id of product in database
 * @param name - name of product
 * @param createdDate - date when product was created
 * @param productFolderName - name of folder where product images are stored
 * @param description - description of product
 * @param shop - shop where product was bought
 * @param type - type of product
 * @param isCreated - flag that shows if product was created or not. It is needed to show only created products in list
 *
 * When we use a primary key of type Int or Long and pass 0 as its value, Room will auto-generate a new value for the primary key colum
 */
@Entity(
    tableName = productsTable
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
