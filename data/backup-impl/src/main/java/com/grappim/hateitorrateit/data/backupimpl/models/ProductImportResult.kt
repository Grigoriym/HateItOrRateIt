package com.grappim.hateitorrateit.data.backupimpl.models

internal data class ProductImportResult(
    val importedProducts: Int,
    val importedImages: Int,
    val failedImages: List<String>,
    val warnings: List<String>,
    val skippedProducts: List<String> = emptyList()
)
