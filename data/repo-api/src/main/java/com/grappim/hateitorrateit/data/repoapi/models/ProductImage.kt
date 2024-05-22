package com.grappim.hateitorrateit.data.repoapi.models

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
