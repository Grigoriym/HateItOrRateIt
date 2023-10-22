package com.grappim.domain

data class DocumentFileData(
    val name: String,
    val mimeType: String,
    val uriPath: String,
    val uriString: String,
    val size: Long,
    val md5: String
)