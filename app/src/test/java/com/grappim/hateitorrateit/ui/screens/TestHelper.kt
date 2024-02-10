package com.grappim.hateitorrateit.ui.screens

import android.net.Uri
import com.grappim.hateitorrateit.domain.CreateProduct
import com.grappim.hateitorrateit.domain.DraftProduct
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.domain.Product
import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.testing.getRandomLong
import com.grappim.hateitorrateit.testing.getRandomString
import com.grappim.hateitorrateit.testing.getRandomUri
import com.grappim.hateitorrateit.testing.nowDate
import com.grappim.hateitorrateit.utils.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.models.ImageData
import java.io.File

const val PRODUCT_ID = 9012L
const val IMAGE_ID = 1276L
const val DESCRIPTION = "description_test"
const val NAME = "name_Test"
const val SHOP = "chop_test"
const val PRODUCT_FOLDER_NAME = "product_folder_name"
const val URI_STRING = "delicata"

val uri: Uri = Uri.parse(URI_STRING)
val TYPE = HateRateType.RATE

val draftProduct = DraftProduct(
    id = PRODUCT_ID,
    date = nowDate,
    productFolderName = PRODUCT_FOLDER_NAME,
    type = TYPE
)

val editProductImages = listOf(
    ProductImageData(
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

val imageData = ImageData(
    imageId = IMAGE_ID,
    uri = uri,
    name = "Kim Hyde",
    size = 3625,
    mimeType = "potenti",
    md5 = "nunc",
    isEdit = false
)

fun createEditProduct(images: List<ProductImageData> = emptyList()): Product = Product(
    id = PRODUCT_ID,
    name = NAME,
    images = images,
    createdDate = nowDate,
    productFolderName = PRODUCT_FOLDER_NAME,
    description = DESCRIPTION,
    shop = SHOP,
    type = TYPE
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

val createProduct = CreateProduct(
    id = PRODUCT_ID,
    name = NAME,
    images = editProductImages,
    createdDate = nowDate,
    productFolderName = PRODUCT_FOLDER_NAME,
    description = DESCRIPTION,
    shop = SHOP,
    type = TYPE
)

fun createImageData(
    imageId: Long? = null,
    newUri: Uri? = null,
    name: String? = null,
    size: Long? = null,
    mimeType: String? = null,
    md5: String? = null,
    isEdit: Boolean? = null
): ImageData = ImageData(
    imageId = imageId ?: getRandomLong(),
    uri = newUri ?: getRandomUri(),
    name = name ?: getRandomString(),
    size = size ?: getRandomLong(),
    mimeType = mimeType ?: getRandomString(),
    md5 = md5 ?: getRandomString(),
    isEdit = isEdit ?: false
)

fun createCameraTakePictureData(): CameraTakePictureData = CameraTakePictureData(
    uri = getRandomUri(),
    file = File(getRandomString())
)
