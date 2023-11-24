package com.grappim.hateitorrateit.data.repoapi

import com.grappim.hateitorrateit.domain.CreateProduct
import com.grappim.hateitorrateit.domain.DraftProduct
import com.grappim.hateitorrateit.domain.EmptyFileData
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.domain.Product
import com.grappim.hateitorrateit.domain.ProductImageData
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    suspend fun getProductById(id: Long): Product

    suspend fun updateProduct(
        id: Long,
        name: String,
        description: String,
        shop: String,
        type: HateRateType,
    )

    suspend fun updateImagesInProduct(
        id: Long,
        files: List<ProductImageData>
    )

    suspend fun addDraftProduct(): DraftProduct

    suspend fun getEmptyFiles(): List<EmptyFileData>

    suspend fun deleteEmptyFiles()

    suspend fun deleteProductImage(id: Long, name: String)

    fun getProductsFlow(
        query: String,
        type: HateRateType?,
    ): Flow<List<Product>>

    suspend fun addProduct(product: CreateProduct)

    suspend fun removeProductById(id: Long)
}
