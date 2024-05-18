package com.grappim.hateitorrateit.data.repoapi

import com.grappim.hateitorrateit.domain.ProductImage

interface BackupImagesRepository {

    suspend fun insertImages(productId: Long, images: List<ProductImage>)

    suspend fun deleteImages(productId: Long, images: List<ProductImage>)

    suspend fun deleteImagesByProductId(productId: Long)

    suspend fun getAllByProductId(productId: Long): List<ProductImage>
}
