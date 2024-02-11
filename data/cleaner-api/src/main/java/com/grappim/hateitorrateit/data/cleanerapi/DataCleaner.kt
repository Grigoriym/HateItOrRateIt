package com.grappim.hateitorrateit.data.cleanerapi

import com.grappim.hateitorrateit.domain.ProductImageData

interface DataCleaner {

    suspend fun clearProductImage(productId: Long, imageName: String, uriString: String): Boolean

    suspend fun deleteProductFileData(productId: Long, list: List<ProductImageData>)

    suspend fun clearProductData(productId: Long, productFolderName: String)

    suspend fun deleteTempFolder(productFolderName: String)

    suspend fun deleteBackupFolder(productFolderName: String)

    suspend fun clearAllData()
}
