package com.grappim.hateitorrateit.data.repoapi

import com.grappim.hateitorrateit.domain.ProductImageData

interface BackupImagesRepository {

    suspend fun insertImages(productId: Long, images: List<ProductImageData>)

    suspend fun deleteImages(productId: Long, images: List<ProductImageData>)

    suspend fun deleteImagesByProductId(productId: Long)

    suspend fun getAllByProductId(productId: Long): List<ProductImageData>
}
