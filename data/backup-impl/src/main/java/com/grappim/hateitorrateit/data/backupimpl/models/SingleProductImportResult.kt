package com.grappim.hateitorrateit.data.backupimpl.models

internal class SingleProductImportResult(
    val success: Boolean,
    val imageCount: Int,
    val failedImages: List<String>
)
