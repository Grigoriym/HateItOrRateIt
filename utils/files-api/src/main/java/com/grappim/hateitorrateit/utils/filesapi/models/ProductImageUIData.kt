package com.grappim.hateitorrateit.utils.filesapi.models

import android.net.Uri

data class ProductImageUIData(
    val imageId: Long = 0L,
    val uri: Uri,
    val name: String,
    val size: Long,
    val mimeType: String,
    val md5: String,
    val isEdit: Boolean = false
)
