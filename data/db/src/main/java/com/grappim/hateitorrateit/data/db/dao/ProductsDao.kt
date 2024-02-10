package com.grappim.hateitorrateit.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.grappim.hateitorrateit.data.db.entities.ProductEntity
import com.grappim.hateitorrateit.data.db.entities.ProductImageDataEntity
import com.grappim.hateitorrateit.data.db.entities.ProductWithImagesEntity
import com.grappim.hateitorrateit.domain.HateRateType
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(productEntity: ProductEntity): Long

    @[Transaction Query("SELECT * FROM products_table WHERE isCreated=1 ORDER BY createdDate DESC")]
    fun getAllProductsFlow(): Flow<List<ProductWithImagesEntity>>

    @[Transaction RawQuery]
    fun getAllProductsByRawQueryFlow(query: SupportSQLiteQuery): Flow<List<ProductWithImagesEntity>>

    @[Transaction Query("SELECT * FROM products_table WHERE productId = :id LIMIT 1")]
    suspend fun getProductById(id: Long): ProductWithImagesEntity

    @Query("DELETE FROM products_table WHERE productId = :id")
    suspend fun deleteById(id: Long)

    @Transaction
    suspend fun updateProductAndImages(
        id: Long,
        name: String,
        description: String,
        shop: String,
        type: HateRateType,
        files: List<ProductImageDataEntity>
    ) {
        updateProduct(id, name, description, shop, type)
        upsertImages(files)
    }

    @Query(
        "UPDATE products_table SET name=:name, " +
            "description=:description, shop=:shop, " +
            "type=:type " +
            "WHERE productId=:id"
    )
    suspend fun updateProduct(
        id: Long,
        name: String,
        description: String,
        shop: String,
        type: HateRateType
    )

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProduct(productEntity: ProductEntity)

    @Transaction
    suspend fun deleteProductAndImagesById(productId: Long) {
        deleteById(productId)
        deleteImagesByProductId(productId)
    }

    @Transaction
    suspend fun updateProductAndImages(
        productEntity: ProductEntity,
        images: List<ProductImageDataEntity>
    ) {
        updateProduct(productEntity)
        insertImages(images)
    }

    @Query("UPDATE products_table SET productFolderName=:folder WHERE productId=:id")
    suspend fun updateProductFolderName(folder: String, id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(list: List<ProductImageDataEntity>)

    @Upsert
    suspend fun upsertImages(list: List<ProductImageDataEntity>)

    @Query("DELETE FROM product_image_data_table WHERE productId = :productId")
    suspend fun deleteImagesByProductId(productId: Long)

    @Query("DELETE FROM product_image_data_table WHERE productId = :productId AND name=:name")
    suspend fun deleteProductImageByIdAndName(productId: Long, name: String)

    @[Transaction Query("SELECT * FROM products_table WHERE isCreated=0")]
    suspend fun getEmptyFiles(): List<ProductWithImagesEntity>

    @Query("DELETE FROM products_table WHERE isCreated=0")
    suspend fun deleteEmptyFiles()
}
