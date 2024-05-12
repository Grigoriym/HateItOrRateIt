package com.grappim.hateitorrateit.utils.filesapi.mappers

import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.utils.filesapi.models.ImageData

interface ImageDataMapper {
    suspend fun toProductImageData(imageData: ImageData): ProductImageData

    suspend fun toProductImageDataList(list: List<ImageData>): List<ProductImageData>

    suspend fun toImageData(productImageData: ProductImageData): ImageData

    suspend fun toImageDataList(list: List<ProductImageData>): List<ImageData>
}
