package com.grappim.hateitorrateit.model

import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.domain.Product
import com.grappim.hateitorrateit.domain.ProductImage
import java.time.OffsetDateTime

const val DATE = "15 15 15 15 15"

fun getImages() = listOf(
    ProductImage(
        imageId = 1,
        name = "Test Product",
        mimeType = "libris",
        uriPath = "uri",
        uriString = "uri",
        size = 4074,
        md5 = "option",
        isEdit = false
    )
)

fun createProduct() = Product(
    id = 1L,
    name = "Test Product",
    images = listOf(
        ProductImage(
            imageId = 1,
            name = "Test Product",
            mimeType = "libris",
            uriPath = "uri",
            uriString = "uri",
            size = 4074,
            md5 = "option",
            isEdit = false
        )
    ),
    createdDate = OffsetDateTime.now(),
    productFolderName = "folderName",
    description = "Description",
    shop = "Shop",
    type = HateRateType.HATE
)

fun createProductListUI() = ProductListUI(
    id = "1",
    name = "Test Product",
    createdDate = DATE,
    previewUriString = "uri",
    productFolderName = "folderName",
    shop = "Shop",
    type = HateRateType.HATE
)

fun createProductDetailsUi() = ProductDetailsUi(
    id = "1",
    name = "Test Product",
    createdDate = DATE,
    productFolderName = "folderName",
    shop = "Shop",
    description = "Description",
    images = getImages(),
    type = HateRateType.HATE
)

fun createProductDetailsImageUi() = ProductDetailsImageUi(
    images = getImages()
)
