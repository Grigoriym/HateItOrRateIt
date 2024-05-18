package com.grappim.hateitorrateit.domain

data class ProductImage(
    val imageId: Long = 0L,
    val name: String,
    val mimeType: String,
    val uriPath: String,
    val uriString: String,
    val size: Long,
    val md5: String,
    val isEdit: Boolean = false
)
