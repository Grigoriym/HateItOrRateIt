package com.grappim.hateitorrateit.data.repoapi

import com.grappim.hateitorrateit.data.repoapi.models.CreateProduct
import com.grappim.hateitorrateit.data.repoapi.models.DraftProduct
import com.grappim.hateitorrateit.data.repoapi.models.EmptyFile
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.data.repoapi.models.Product
import com.grappim.hateitorrateit.data.repoapi.models.ProductImage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    suspend fun getProductById(productId: Long): Product

    suspend fun updateProduct(
        id: Long,
        name: String,
        description: String,
        shop: String,
        type: HateRateType
    )

    suspend fun updateProduct(product: Product)

    suspend fun updateImagesInProduct(id: Long, images: List<ProductImage>)

    suspend fun updateProductWithImages(product: Product, images: List<ProductImage>)

    suspend fun addDraftProduct(): DraftProduct

    suspend fun getEmptyFiles(): List<EmptyFile>

    suspend fun deleteEmptyFiles()

    suspend fun deleteProductImage(productId: Long, imageName: String)

    fun getProductsFlow(query: String, type: HateRateType?): Flow<ImmutableList<Product>>

    suspend fun addProduct(product: CreateProduct)

    suspend fun deleteProductById(productId: Long)
}
