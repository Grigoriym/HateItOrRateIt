package com.grappim.hateitorrateit.domain

data class DocumentFileData(
    val name: String,
    val mimeType: String,
    val uriPath: String,
    val uriString: String,
    val size: Long,
    @Deprecated("remove it")
    val previewUriString: String? = null,
    @Deprecated("remove it")
    val previewUriPath: String? = null,
    val md5: String
)