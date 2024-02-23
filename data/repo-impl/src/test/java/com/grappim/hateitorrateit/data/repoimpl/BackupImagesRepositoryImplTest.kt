package com.grappim.hateitorrateit.data.repoimpl

import com.grappim.hateitorrateit.data.db.dao.BackupImagesDao
import com.grappim.hateitorrateit.data.db.entities.BackupProductImageDataEntity
import com.grappim.hateitorrateit.domain.ProductImageData
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class BackupImagesRepositoryImplTest {

    private lateinit var repository: BackupImagesRepositoryImpl
    private val backupImagesDao: BackupImagesDao = mockk()
    private val backupProductMapper: BackupProductMapper = mockk()

    @Before
    fun setup() {
        repository = BackupImagesRepositoryImpl(backupImagesDao, backupProductMapper)
    }

    @Test
    fun `insertImages calls Dao insert with correct entities`() = runTest {
        coEvery { backupImagesDao.insert(any()) } just Runs

        val productId = 1L
        val images = listOf(
            ProductImageData(
                imageId = 1L,
                name = "Test",
                mimeType = "image/png",
                uriPath = "path",
                uriString = "uri",
                size = 100L,
                md5 = "md5"
            )
        )
        val entities = listOf(
            BackupProductImageDataEntity(
                imageId = 1L,
                productId = productId,
                name = "Test",
                mimeType = "image/png",
                size = 100L,
                uriPath = "path",
                uriString = "uri",
                md5 = "md5"
            )
        )

        coEvery {
            backupProductMapper.toBackupProductImageDataEntity(
                productId,
                images
            )
        } returns entities

        repository.insertImages(productId, images)

        coVerify { backupImagesDao.insert(entities) }
    }

    @Test
    fun `deleteImages calls Dao delete with correct entities`() = runTest {
        coEvery { backupImagesDao.delete(any()) } just Runs

        val productId = 1L
        val images = listOf(
            ProductImageData(
                imageId = 1L,
                name = "Test",
                mimeType = "image/png",
                uriPath = "path",
                uriString = "uri",
                size = 100L,
                md5 = "md5"
            )
        )
        val entities = listOf(
            BackupProductImageDataEntity(
                imageId = 1L,
                productId = productId,
                name = "Test",
                mimeType = "image/png",
                size = 100L,
                uriPath = "path",
                uriString = "uri",
                md5 = "md5"
            )
        )

        coEvery {
            backupProductMapper.toBackupProductImageDataEntity(
                productId,
                images
            )
        } returns entities

        repository.deleteImages(productId, images)

        coVerify { backupImagesDao.delete(entities) }
    }

    @Test
    fun `deleteImagesByProductId calls Dao deleteImagesByProductId`() = runTest {
        coEvery { backupImagesDao.deleteImagesByProductId(any()) } just Runs

        val productId = 1L

        repository.deleteImagesByProductId(productId)

        coVerify { backupImagesDao.deleteImagesByProductId(productId) }
    }

    @Test
    fun `getAllByProductId calls Dao getAllImagesByProductId and mapper correctly`() = runTest {
        val productId = 1L
        val entities = listOf(
            BackupProductImageDataEntity(
                imageId = 1L,
                productId = productId,
                name = "Test",
                mimeType = "image/png",
                size = 100L,
                uriPath = "path",
                uriString = "uri",
                md5 = "md5"
            )
        )
        val images = listOf(
            ProductImageData(
                imageId = 1L,
                name = "Test",
                mimeType = "image/png",
                uriPath = "path",
                uriString = "uri",
                size = 100L,
                md5 = "md5"
            )
        )

        coEvery { backupImagesDao.getAllImagesByProductId(productId) } returns entities
        coEvery { backupProductMapper.toProductImageData(entities) } returns images

        val result = repository.getAllByProductId(productId)

        coVerify { backupImagesDao.getAllImagesByProductId(productId) }
        coVerify { backupProductMapper.toProductImageData(entities) }
        assertEquals(result, images)
    }
}
