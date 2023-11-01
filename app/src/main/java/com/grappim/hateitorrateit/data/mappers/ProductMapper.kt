package com.grappim.hateitorrateit.data.mappers

import com.grappim.domain.Product
import com.grappim.domain.ProductImageData
import com.grappim.hateitorrateit.data.db.entities.ProductEntity
import com.grappim.hateitorrateit.data.db.entities.ProductImageDataEntity
import com.grappim.hateitorrateit.data.db.entities.ProductWithImagesEntity
import com.grappim.hateitorrateit.model.CreateProduct

fun ProductWithImagesEntity.toProduct() =
    this.productEntity.toProduct(this.files)

fun ProductEntity.toProduct(
    imageEntities: List<ProductImageDataEntity>?
): Product =
    Product(
        id = this.productId,
        name = this.name,
        filesUri = imageEntities?.map { productImageDataEntity ->
            ProductImageData(
                name = productImageDataEntity.name,
                mimeType = productImageDataEntity.mimeType,
                uriPath = productImageDataEntity.uriPath,
                uriString = productImageDataEntity.uriString,
                size = productImageDataEntity.size,
                md5 = productImageDataEntity.md5
            )
        } ?: emptyList(),
        createdDate = this.createdDate,
        productFolderName = this.productFolderName,
        description = this.description,
        shop = this.shop,
        type = this.type
    )

fun List<ProductImageData>.toEntities(productId: Long) =
    this.map { it.toEntity(productId) }

fun ProductImageData.toEntity(productId: Long): ProductImageDataEntity =
    ProductImageDataEntity(
        name = this.name,
        mimeType = this.mimeType,
        uriPath = this.uriPath,
        uriString = this.uriString,
        size = this.size,
        md5 = this.md5,
        productId = productId
    )

fun CreateProduct.toEntity(): ProductEntity =
    ProductEntity(
        productId = this.id,
        name = this.name,
        createdDate = this.createdDate,
        productFolderName = this.productFolderName,
        description = this.description,
        shop = this.shop,
        type = this.type,
        isCreated = true,
    )

fun CreateProduct.toImageDataEntityList(): List<ProductImageDataEntity> =
    filesUri.map {
        ProductImageDataEntity(
            productId = this.id,
            name = it.name,
            mimeType = it.mimeType,
            size = it.size,
            uriPath = it.uriPath,
            uriString = it.uriString,
            md5 = it.md5
        )
    }
