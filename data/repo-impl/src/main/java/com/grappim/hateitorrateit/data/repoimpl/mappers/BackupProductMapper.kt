package com.grappim.hateitorrateit.data.repoimpl.mappers

import com.grappim.hateitorrateit.commons.IoDispatcher
import com.grappim.hateitorrateit.data.db.entities.BackupProductImageDataEntity
import com.grappim.hateitorrateit.data.repoapi.models.ProductImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupProductMapper @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun toBackupProductImageDataEntity(productId: Long, images: List<ProductImage>) =
        withContext(ioDispatcher) {
            images.map {
                BackupProductImageDataEntity(
                    imageId = it.imageId,
                    productId = productId,
                    name = it.name,
                    mimeType = it.mimeType,
                    size = it.size,
                    uriPath = it.uriPath,
                    uriString = it.uriString,
                    md5 = it.md5
                )
            }
        }

    suspend fun toProductImageData(list: List<BackupProductImageDataEntity>): List<ProductImage> =
        withContext(ioDispatcher) {
            list.map {
                ProductImage(
                    imageId = it.imageId,
                    name = it.name,
                    mimeType = it.mimeType,
                    size = it.size,
                    uriPath = it.uriPath,
                    uriString = it.uriString,
                    md5 = it.md5
                )
            }
        }
}
