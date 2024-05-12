package com.grappim.hateitorrateit.utils.filesapi.productmanager

interface ProductImageManager {
    suspend fun copyToBackupFolder(productFolderName: String)

    suspend fun moveFromTempToOriginalFolder(productFolderName: String)

    suspend fun moveFromBackupToOriginalFolder(productFolderName: String)
}
