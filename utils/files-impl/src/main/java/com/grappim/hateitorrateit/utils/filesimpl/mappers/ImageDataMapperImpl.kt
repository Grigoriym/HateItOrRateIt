package com.grappim.hateitorrateit.utils.filesimpl.mappers

import com.grappim.hateitorrateit.commons.IoDispatcher
import com.grappim.hateitorrateit.data.repoapi.models.ProductImage
import com.grappim.hateitorrateit.utils.filesapi.mappers.ImageDataMapper
import com.grappim.hateitorrateit.utils.filesapi.models.ProductImageUIData
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
    override suspend fun toProductImageData(productImageUIData: ProductImageUIData): ProductImage =
        withContext(ioDispatcher) {
            ProductImage(
                imageId = productImageUIData.imageId,
                name = productImageUIData.name,
                mimeType = productImageUIData.mimeType,
                uriPath = productImageUIData.uri.path ?: "",
                uriString = productImageUIData.uri.toString(),
                size = productImageUIData.size,
                md5 = productImageUIData.md5,
                isEdit = productImageUIData.isEdit
            )
        }

    override suspend fun toProductImageDataList(
        list: List<ProductImageUIData>
    ): List<ProductImage> = withContext(ioDispatcher) {
        list.map { productImageUIData: ProductImageUIData ->
            toProductImageData(productImageUIData)
        }
    }

    override suspend fun toImageData(productImage: ProductImage): ProductImageUIData =
        withContext(ioDispatcher) {
            ProductImageUIData(
                imageId = productImage.imageId,
                uri = uriParser.parse(productImage.uriString),
                name = productImage.name,
                size = productImage.size,
                mimeType = productImage.mimeType,
                md5 = productImage.md5,
                isEdit = productImage.isEdit
            )
        }

    override suspend fun toImageDataList(list: List<ProductImage>): List<ProductImageUIData> =
        withContext(ioDispatcher) {
            list.map { productImage: ProductImage ->
                toImageData(productImage)
            }
        }
}
