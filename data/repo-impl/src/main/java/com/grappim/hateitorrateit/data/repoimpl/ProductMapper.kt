package com.grappim.hateitorrateit.data.repoimpl

import androidx.annotation.VisibleForTesting
import com.grappim.hateitorrateit.commons.IoDispatcher
import com.grappim.hateitorrateit.data.db.entities.ProductEntity
import com.grappim.hateitorrateit.data.db.entities.ProductImageDataEntity
import com.grappim.hateitorrateit.data.db.entities.ProductWithImagesEntity
import com.grappim.hateitorrateit.domain.CreateProduct
import com.grappim.hateitorrateit.domain.EmptyFileData
import com.grappim.hateitorrateit.domain.Product
import com.grappim.hateitorrateit.domain.ProductImageData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductMapper @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun toProduct(productWithImagesEntity: ProductWithImagesEntity): Product =
        withContext(ioDispatcher) {
            val entity = productWithImagesEntity.productEntity
            val images = productWithImagesEntity.files
            Product(
                id = entity.productId,
                name = entity.name,
                images = images?.map { productImageDataEntity ->
                    toProductImageData(productImageDataEntity)
                } ?: emptyList(),
                createdDate = entity.createdDate,
                productFolderName = entity.productFolderName,
                description = entity.description,
                shop = entity.shop,
                type = entity.type
            )
        }

    suspend fun toProduct(
        productEntity: ProductEntity,
        images: List<ProductImageDataEntity>?
    ): Product = withContext(ioDispatcher) {
        Product(
            id = productEntity.productId,
            name = productEntity.name,
            images = images?.map { productImageDataEntity ->
                toProductImageData(productImageDataEntity)
            } ?: emptyList(),
            createdDate = productEntity.createdDate,
            productFolderName = productEntity.productFolderName,
            description = productEntity.description,
            shop = productEntity.shop,
            type = productEntity.type
        )
    }

    suspend fun toProductImageDataEntityList(
        productId: Long,
        images: List<ProductImageData>
    ): List<ProductImageDataEntity> = withContext(ioDispatcher) {
        images.map {
            toProductImageDataEntity(
                productId = productId,
                productImageData = it
            )
        }
    }

    suspend fun toProductImageDataEntity(
        productId: Long,
        productImageData: ProductImageData
    ): ProductImageDataEntity = withContext(ioDispatcher) {
        ProductImageDataEntity(
            imageId = productImageData.imageId,
            name = productImageData.name,
            mimeType = productImageData.mimeType,
            uriPath = productImageData.uriPath,
            uriString = productImageData.uriString,
            size = productImageData.size,
            md5 = productImageData.md5,
            productId = productId
        )
    }

    suspend fun toEmptyFileDataList(list: List<ProductWithImagesEntity>): List<EmptyFileData> =
        withContext(ioDispatcher) {
            list.map { productWithImagesEntity ->
                EmptyFileData(
                    id = productWithImagesEntity.productEntity.productId,
                    productFolderName = productWithImagesEntity.productEntity.productFolderName
                )
            }
        }

    suspend fun toProductEntity(createProduct: CreateProduct): ProductEntity =
        withContext(ioDispatcher) {
            ProductEntity(
                productId = createProduct.id,
                name = createProduct.name,
                createdDate = createProduct.createdDate,
                productFolderName = createProduct.productFolderName,
                description = createProduct.description,
                shop = createProduct.shop,
                type = createProduct.type,
                isCreated = true
            )
        }

    suspend fun toProductEntity(product: Product): ProductEntity = withContext(ioDispatcher) {
        ProductEntity(
            productId = product.id,
            name = product.name,
            createdDate = product.createdDate,
            productFolderName = product.productFolderName,
            description = product.description,
            shop = product.shop,
            type = product.type,
            isCreated = true
        )
    }

    suspend fun toProductImageDataEntityList(
        createProduct: CreateProduct
    ): List<ProductImageDataEntity> = withContext(ioDispatcher) {
        createProduct.images.map {
            ProductImageDataEntity(
                productId = createProduct.id,
                name = it.name,
                mimeType = it.mimeType,
                size = it.size,
                uriPath = it.uriPath,
                uriString = it.uriString,
                md5 = it.md5
            )
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun toProductImageData(productImageDataEntity: ProductImageDataEntity) = ProductImageData(
        imageId = productImageDataEntity.imageId,
        name = productImageDataEntity.name,
        mimeType = productImageDataEntity.mimeType,
        uriPath = productImageDataEntity.uriPath,
        uriString = productImageDataEntity.uriString,
        size = productImageDataEntity.size,
        md5 = productImageDataEntity.md5
    )
}
