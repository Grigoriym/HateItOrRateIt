package com.grappim.hateitorrateit.utils.filesapi.mappers

import com.grappim.hateitorrateit.data.repoapi.models.ProductImage
import com.grappim.hateitorrateit.utils.filesapi.models.ProductImageUIData
import kotlinx.collections.immutable.ImmutableList

interface ImageDataMapper {
    suspend fun toProductImageData(productImageUIData: ProductImageUIData): ProductImage

    suspend fun toProductImageDataList(list: List<ProductImageUIData>): List<ProductImage>

    suspend fun toImageData(productImage: ProductImage): ProductImageUIData

    suspend fun toImageDataList(list: List<ProductImage>): ImmutableList<ProductImageUIData>
}
