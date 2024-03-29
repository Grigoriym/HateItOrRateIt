package com.grappim.hateitorrateit.data.repoimpl

import com.grappim.hateitorrateit.data.db.dao.BackupImagesDao
import com.grappim.hateitorrateit.data.repoapi.BackupImagesRepository
import com.grappim.hateitorrateit.domain.ProductImageData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupImagesRepositoryImpl @Inject constructor(
    private val backupImagesDao: BackupImagesDao,
    private val backupProductMapper: BackupProductMapper
) : BackupImagesRepository {
    override suspend fun insertImages(productId: Long, images: List<ProductImageData>) {
        val entities = backupProductMapper.toBackupProductImageDataEntity(productId, images)
        backupImagesDao.insert(entities)
    }

    override suspend fun deleteImages(productId: Long, images: List<ProductImageData>) {
        val entities = backupProductMapper.toBackupProductImageDataEntity(productId, images)
        backupImagesDao.delete(entities)
    }

    override suspend fun deleteImagesByProductId(productId: Long) {
        backupImagesDao.deleteImagesByProductId(productId)
    }

    override suspend fun getAllByProductId(productId: Long): List<ProductImageData> {
        val result = backupImagesDao.getAllImagesByProductId(productId)
        return backupProductMapper.toProductImageData(result)
    }
}
