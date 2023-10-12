package com.grappim.hateitorrateit.utils

import android.net.Uri

data class FileData(
    val uri: Uri,
    val name: String,
    val size: Long,
    val sizeToDemonstrate: String,
    val mimeType: String,
    val mimeTypeToDemonstrate: String,
    var preview: Any? = null,
    val md5: String
)
