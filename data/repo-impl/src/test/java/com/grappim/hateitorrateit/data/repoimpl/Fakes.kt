package com.grappim.hateitorrateit.data.repoimpl

import com.grappim.hateitorrateit.data.db.entities.ProductEntity
import com.grappim.hateitorrateit.data.db.entities.ProductImageDataEntity
import com.grappim.hateitorrateit.data.db.entities.ProductWithImagesEntity
import com.grappim.hateitorrateit.domain.CreateProduct
import com.grappim.hateitorrateit.domain.EmptyFileData
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.domain.Product
import com.grappim.hateitorrateit.domain.ProductImageData
import java.time.OffsetDateTime

const val ID = 1L
const val NAME = "test_name"
const val FOLDER_NAME = "folder_name"

val nowDate: OffsetDateTime = OffsetDateTime.now()

fun getProductImageDataEntityList() = listOf(
    getProductImageDataEntity()
)

fun getProductImageDataEntity() = ProductImageDataEntity(
    name = NAME,
    mimeType = "mimeType",
    uriPath = "uriPath",
    uriString = "uriString",
    size = 12,
    md5 = "md5",
    productId = ID
)

fun getProductWithImagesEntityList() = listOf(
    getProductWithImagesEntity()
)

fun getProductWithImagesEntity() = ProductWithImagesEntity(
    productEntity = ProductEntity(
        productId = ID,
        name = NAME,
        createdDate = nowDate,
        productFolderName = FOLDER_NAME,
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
    productFolderName = FOLDER_NAME
)

fun getProductImageData() = ProductImageData(
    name = NAME,
    mimeType = "mimeType",
    uriPath = "uriPath",
    uriString = "uriString",
    size = 12,
    md5 = "md5"
)

fun getProductImageDataList() = listOf(
    getProductImageData()
)

fun getProduct(filesUri: List<ProductImageData> = getProductImageDataList()): Product = Product(
    id = ID,
    name = NAME,
    images = filesUri,
    createdDate = nowDate,
    productFolderName = FOLDER_NAME,
    description = "description",
    shop = "shop",
    type = HateRateType.HATE
)

fun getProductEntity(): ProductEntity = ProductEntity(
    productId = ID,
    name = NAME,
    createdDate = nowDate,
    productFolderName = FOLDER_NAME,
    description = "description",
    shop = "shop",
    type = HateRateType.HATE,
    isCreated = true
)

fun getCreateProduct() = CreateProduct(
    id = ID,
    name = NAME,
    images = getProductImageDataList(),
    createdDate = nowDate,
    productFolderName = FOLDER_NAME,
    description = "description",
    shop = "shop",
    type = HateRateType.HATE
)
