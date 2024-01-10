package com.grappim.hateitorrateit.data.db

import com.grappim.hateitorrateit.data.db.entities.ProductEntity
import com.grappim.hateitorrateit.data.db.entities.ProductImageDataEntity
import com.grappim.hateitorrateit.domain.HateRateType
import java.time.OffsetDateTime

const val PRODUCT_IMAGE_NAME = "product_image_name"
const val PRODUCT_IMAGE_NAME_2 = "product_image_name_2"

fun getNowDate(): OffsetDateTime = OffsetDateTime.now()

fun getProductEntity(): ProductEntity =
    ProductEntity(
        name = "Myles Burke",
        createdDate = getNowDate(),
        productFolderName = "Sophia House",
        description = "sale",
        shop = "quis",
        type = HateRateType.RATE,
        isCreated = true
    )

fun getProductImageList(id: Long): List<ProductImageDataEntity> = listOf(
    ProductImageDataEntity(
        productId = id,
        name = PRODUCT_IMAGE_NAME,
        mimeType = "pri",
        size = 7080,
        uriPath = "a",
        uriString = "praesent",
        md5 = "malorum"
    ),
    ProductImageDataEntity(
        productId = id,
        name = PRODUCT_IMAGE_NAME_2,
        mimeType = "dissentiunt",
        size = 5041,
        uriPath = "venenatis",
        uriString = "atqui",
        md5 = "reque"
    )
)