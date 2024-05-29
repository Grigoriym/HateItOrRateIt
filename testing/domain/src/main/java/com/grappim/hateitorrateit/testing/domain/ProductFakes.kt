@file:Suppress("MagicNumber")

package com.grappim.hateitorrateit.testing.domain

import com.grappim.hateitorrateit.data.repoapi.models.DraftProduct
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.data.repoapi.models.Product
import com.grappim.hateitorrateit.data.repoapi.models.ProductImage
import java.time.OffsetDateTime
import kotlin.random.Random

const val PRODUCT_ID = 9012L
const val IMAGE_ID = 1276L
const val DESCRIPTION = "description_test"
const val NAME = "name_Test"
const val SHOP = "chop_test"
const val PRODUCT_FOLDER_NAME = "product_folder_name"
const val URI_STRING = "delicata"
val TYPE = HateRateType.RATE

fun getFakeProduct() = Product(
    id = getRandomLong(),
    name = getRandomString(),
    images = listOf(
        ProductImage(
            imageId = getRandomLong(),
            name = getRandomString(),
            mimeType = getRandomString(),
            uriPath = getRandomString(),
            uriString = getRandomString(),
            size = getRandomLong(),
            md5 = getRandomString(),
            isEdit = getRandomBoolean()
        )
    ),
    createdDate = OffsetDateTime.now(),
    productFolderName = getRandomString(),
    description = getRandomString(),
    shop = getRandomString(),
    type = HateRateType.HATE
)

val draftProduct = DraftProduct(
    id = PRODUCT_ID,
    date = nowDate,
    productFolderName = PRODUCT_FOLDER_NAME,
    type = TYPE
)

val editProductImages = listOf(
    ProductImage(
        imageId = 2616,
        name = "Kim Hyde",
        mimeType = "potenti",
        uriPath = "voluptatum",
        uriString = URI_STRING,
        size = 3625,
        md5 = "nunc",
        isEdit = false
    )
)

val editProduct = Product(
    id = PRODUCT_ID,
    name = NAME,
    images = emptyList(),
    createdDate = nowDate,
    productFolderName = PRODUCT_FOLDER_NAME,
    description = DESCRIPTION,
    shop = SHOP,
    type = TYPE
)

fun createEditProduct(images: List<ProductImage> = emptyList()): Product = Product(
    id = PRODUCT_ID,
    name = NAME,
    images = images,
    createdDate = nowDate,
    productFolderName = PRODUCT_FOLDER_NAME,
    description = DESCRIPTION,
    shop = SHOP,
    type = TYPE
)

fun createRandomProductImage() = ProductImage(
    imageId = getRandomLong(),
    name = getRandomString(),
    mimeType = getRandomString(),
    uriPath = getRandomString(),
    uriString = getRandomString(),
    size = getRandomLong(),
    md5 = getRandomString(),
    isEdit = getRandomBoolean()
)

fun createRandomProductImageList(): List<ProductImage> {
    val list = mutableListOf<ProductImage>()
    repeat(Random(1).nextInt(3, 10)) {
        list.add(createRandomProductImage())
    }
    return list.toList()
}

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
