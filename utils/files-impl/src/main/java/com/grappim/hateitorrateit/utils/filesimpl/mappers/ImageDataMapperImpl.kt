package com.grappim.hateitorrateit.utils.filesimpl.mappers

import com.grappim.hateitorrateit.commons.IoDispatcher
import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.utils.filesapi.mappers.ImageDataMapper
import com.grappim.hateitorrateit.utils.filesapi.models.ImageData
import com.grappim.hateitorrateit.utils.filesimpl.UriParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageDataMapperImpl @Inject constructor(
    private val uriParser: UriParser,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ImageDataMapper {
    override suspend fun toProductImageData(imageData: ImageData): ProductImageData =
        withContext(ioDispatcher) {
            ProductImageData(
                imageId = imageData.imageId,
                name = imageData.name,
                mimeType = imageData.mimeType,
                uriPath = imageData.uri.path ?: "",
                uriString = imageData.uri.toString(),
                size = imageData.size,
                md5 = imageData.md5,
                isEdit = imageData.isEdit
            )
        }

    override suspend fun toProductImageDataList(list: List<ImageData>): List<ProductImageData> =
        withContext(ioDispatcher) {
            list.map { imageData: ImageData ->
                toProductImageData(imageData)
            }
        }

    override suspend fun toImageData(productImageData: ProductImageData): ImageData =
        withContext(ioDispatcher) {
            ImageData(
                imageId = productImageData.imageId,
                uri = uriParser.parse(productImageData.uriString),
                name = productImageData.name,
                size = productImageData.size,
                mimeType = productImageData.mimeType,
                md5 = productImageData.md5,
                isEdit = productImageData.isEdit
            )
        }

    override suspend fun toImageDataList(list: List<ProductImageData>): List<ImageData> =
        withContext(ioDispatcher) {
            list.map { productImageData: ProductImageData ->
                toImageData(productImageData)
            }
        }
}
