package com.grappim.hateitorrateit.utils.filesimpl

import javax.inject.Inject
import javax.inject.Singleton

private const val PREFIX = "image/"

private const val JPEG = PREFIX + "jpeg"
private const val PNG = PREFIX + "png"

@Singleton
class MimeTypes @Inject constructor() {

    fun formatMimeType(mimeType: String): String = when (mimeType) {
        PNG -> "png"
        JPEG -> "jpg"
        else -> "unknown"
    }
}
