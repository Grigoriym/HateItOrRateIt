package com.grappim.hateitorrateit.utils

import android.net.Uri

data class FileData(
    val uri: Uri,
    val name: String,
    val size: Long,
    val sizeToDemonstrate: String,
    val mimeType: String,
    val mimeTypeToDemonstrate: String,
    val md5: String
)
