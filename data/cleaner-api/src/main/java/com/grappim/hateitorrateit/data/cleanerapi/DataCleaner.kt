package com.grappim.hateitorrateit.data.cleanerapi

import com.grappim.hateitorrateit.domain.DraftProduct
import com.grappim.hateitorrateit.domain.ProductImageData

interface DataCleaner {
    suspend fun clearProductImage(
        id: Long,
        imageName: String,
        uriString: String,
    ): Boolean

    suspend fun deleteProductFileData(
        id: Long,
        list: List<ProductImageData>
    )

    suspend fun clearProductData(
        id: Long,
        productFolderName: String,
    )

    suspend fun clearProductData(
        draftProduct: DraftProduct
    )

    suspend fun clearAllData(): Boolean
}
