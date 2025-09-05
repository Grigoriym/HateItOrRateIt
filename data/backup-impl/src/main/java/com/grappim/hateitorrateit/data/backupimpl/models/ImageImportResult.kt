package com.grappim.hateitorrateit.data.backupimpl.models

import com.grappim.hateitorrateit.data.repoapi.models.ProductImage

internal data class ImageImportResult(
    val productImages: List<ProductImage>,
    val importedCount: Int,
    val failedImages: List<String>
)
