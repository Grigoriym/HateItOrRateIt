package com.grappim.hateitorrateit.data.cleanerapi

import com.grappim.hateitorrateit.data.repoapi.models.ProductImage

interface DataCleaner {

    suspend fun deleteProductImage(productId: Long, imageName: String, uriString: String): Boolean

    suspend fun deleteProductImages(productId: Long, list: List<ProductImage>)

    suspend fun deleteProductData(productId: Long, productFolderName: String)

    suspend fun deleteTempFolder(productFolderName: String)

    suspend fun deleteBackupFolder(productFolderName: String)

    suspend fun clearAllData()
}
