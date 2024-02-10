package com.grappim.hateitorrateit.data.repoimpl

import com.grappim.hateitorrateit.data.db.dao.BackupImagesDao
import com.grappim.hateitorrateit.data.db.entities.BackupProductImageDataEntity
import com.grappim.hateitorrateit.data.repoapi.BackupImagesRepository
import com.grappim.hateitorrateit.domain.ProductImageData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupImagesRepositoryImpl @Inject constructor(
    private val backupImagesDao: BackupImagesDao
) : BackupImagesRepository {
    override suspend fun insertImages(productId: Long, images: List<ProductImageData>) {
        val entities = images.map {
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

        backupImagesDao.insert(entities)
    }

    override suspend fun deleteImages(productId: Long, images: List<ProductImageData>) {
        val entities = images.map {
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

        backupImagesDao.delete(entities)
    }

    override suspend fun deleteImagesByProductId(productId: Long) {
        backupImagesDao.deleteImagesByProductId(productId)
    }

    override suspend fun getAllByProductId(productId: Long): List<ProductImageData> {
        val result = backupImagesDao.getAllImagesByProductId(productId)
        val mapped = result.map {
            ProductImageData(
                imageId = it.imageId,
                name = it.name,
                mimeType = it.mimeType,
                size = it.size,
                uriPath = it.uriPath,
                uriString = it.uriString,
                md5 = it.md5
            )
        }
        return mapped
    }
}
