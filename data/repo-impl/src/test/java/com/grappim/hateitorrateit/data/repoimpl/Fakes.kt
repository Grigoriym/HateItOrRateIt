package com.grappim.hateitorrateit.data.repoimpl

import com.grappim.hateitorrateit.data.db.entities.ProductEntity
import com.grappim.hateitorrateit.data.db.entities.ProductImageDataEntity
import com.grappim.hateitorrateit.data.db.entities.ProductWithImagesEntity
import com.grappim.hateitorrateit.domain.CreateProduct
import com.grappim.hateitorrateit.domain.DraftProduct
import com.grappim.hateitorrateit.domain.EmptyFileData
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.domain.Product
import com.grappim.hateitorrateit.domain.ProductImageData
import java.time.OffsetDateTime

const val ID = 1L
const val NAME = "test_name"

fun getProductImageDataEntityList() = listOf(
    getProductImageDataEntity()
)

fun getProductImageDataEntity() =
    ProductImageDataEntity(
        name = NAME,
        mimeType = "mimeType",
        uriPath = "uriPath",
        uriString = "uriString",
        size = 12,
        md5 = "md5",
        productId = ID
    )

fun getListOfProductWithImagesEntity() = listOf(
    getProductWithImagesEntity()
)

fun getProductWithImagesEntity() =
    ProductWithImagesEntity(
        productEntity = ProductEntity(
            productId = ID,
            name = NAME,
            createdDate = OffsetDateTime.now(),
            productFolderName = "productFolderName",
            description = "description",
            shop = "shop",
            type = HateRateType.HATE,
            isCreated = true
        ),
        files = listOf(
            getProductImageDataEntity()
        )
    )

fun getListOfEmptyFileData() = listOf(
    getEmptyFileData()
)

fun getEmptyFileData() = EmptyFileData(
    id = ID,
    productFolderName = "productFolderName",
)

fun getProductImageData() = ProductImageData(
    name = "name",
    mimeType = "mimeType",
    uriPath = "uriPath",
    uriString = "uriString",
    size = 12,
    md5 = "md5"
)

fun getProduct(): Product = Product(
    id = ID,
    name = "name",
    filesUri = listOf(
        getProductImageData()
    ),
    createdDate = OffsetDateTime.now(),
    productFolderName = "productFolderName",
    description = "description",
    shop = "shop",
    type = HateRateType.HATE
)

fun getProductEntity(): ProductEntity = ProductEntity(
    productId = ID,
    name = "name",
    createdDate = OffsetDateTime.now(),
    productFolderName = "productFolderName",
    description = "description",
    shop = "shop",
    type = HateRateType.HATE,
    isCreated = true
)

fun getDraftProduct() = DraftProduct(
    id = ID,
    date = OffsetDateTime.now(),
    folderName = "folderName",
    type = HateRateType.HATE
)

fun getCreateProduct() = CreateProduct(
    id = ID,
    name = NAME,
    filesUri = listOf(
        getProductImageData()
    ),
    createdDate = OffsetDateTime.now(),
    productFolderName = "productFolderName",
    description = "description",
    shop = "shop",
    type = HateRateType.HATE
)

fun getNowDate() = OffsetDateTime.now()