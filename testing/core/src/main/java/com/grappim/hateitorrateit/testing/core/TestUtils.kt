package com.grappim.hateitorrateit.testing.core

import android.net.Uri
import com.grappim.hateitorrateit.data.repoapi.models.CreateProduct
import com.grappim.hateitorrateit.testing.domain.DESCRIPTION
import com.grappim.hateitorrateit.testing.domain.NAME
import com.grappim.hateitorrateit.testing.domain.PRODUCT_FOLDER_NAME
import com.grappim.hateitorrateit.testing.domain.PRODUCT_ID
import com.grappim.hateitorrateit.testing.domain.SHOP
import com.grappim.hateitorrateit.testing.domain.TYPE
import com.grappim.hateitorrateit.testing.domain.editProductImages
import com.grappim.hateitorrateit.testing.domain.getRandomLong
import com.grappim.hateitorrateit.testing.domain.getRandomString
import com.grappim.hateitorrateit.testing.domain.nowDate
import com.grappim.hateitorrateit.utils.filesapi.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.filesapi.models.ProductImageUIData
import java.io.File

fun getRandomUri(): Uri {
    val randomString = getRandomString()
    val uriString = "https://grappim.com/$randomString"
    return Uri.parse(uriString)
}

val testException = IllegalStateException("error")

fun createCameraTakePictureData(): CameraTakePictureData = CameraTakePictureData(
    uri = getRandomUri(),
    file = File(getRandomString())
)

fun createImageData(
    imageId: Long? = null,
    newUri: Uri? = null,
    name: String? = null,
    size: Long? = null,
    mimeType: String? = null,
    md5: String? = null,
    isEdit: Boolean? = null
): ProductImageUIData = ProductImageUIData(
    imageId = imageId ?: getRandomLong(),
    uri = newUri ?: getRandomUri(),
    name = name ?: getRandomString(),
    size = size ?: getRandomLong(),
    mimeType = mimeType ?: getRandomString(),
    md5 = md5 ?: getRandomString(),
    isEdit = isEdit ?: false
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
