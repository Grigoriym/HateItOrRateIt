package com.grappim.hateitorrateit.utils

import android.net.Uri

data class ImageData(
    val uri: Uri,
    val name: String,
    val size: Long,
    val mimeType: String,
    val md5: String
)
