package com.grappim.hateitorrateit.utils

object MimeTypes {

    val images = listOf(
        Image.PNG,
        Image.JPEG
    )

    fun formatMimeType(mimeType: String): String =
        when (mimeType) {
            Image.PNG -> "png"
            Image.JPEG -> "jpg"

            else -> "unknown"
        }

    object Image {
        const val PREFIX = "image/"

        const val JPEG = PREFIX + "jpeg"
        const val PNG = PREFIX + "png"
    }
}
