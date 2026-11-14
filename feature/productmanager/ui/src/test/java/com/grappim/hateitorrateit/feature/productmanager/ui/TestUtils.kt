package com.grappim.hateitorrateit.feature.productmanager.ui

import android.net.Uri
import com.grappim.hateitorrateit.testing.core.fakeUri
import com.grappim.hateitorrateit.testing.domain.IMAGE_ID
import com.grappim.hateitorrateit.testing.domain.URI_STRING
import com.grappim.hateitorrateit.utils.filesapi.models.ProductImageUIData

val uri: Uri = fakeUri(URI_STRING)

val productImageUIData = ProductImageUIData(
    imageId = IMAGE_ID,
    uri = uri,
    name = "Kim Hyde",
    size = 3625,
    mimeType = "potenti",
    md5 = "nunc",
    isEdit = false
)
