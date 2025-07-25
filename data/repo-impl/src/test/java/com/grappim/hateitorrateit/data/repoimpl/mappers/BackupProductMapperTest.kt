package com.grappim.hateitorrateit.data.repoimpl.mappers

import com.grappim.hateitorrateit.data.db.entities.BackupProductImageDataEntity
import com.grappim.hateitorrateit.data.repoapi.models.ProductImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class BackupProductMapperTest {

    private val mapper = BackupProductMapper(ioDispatcher = UnconfinedTestDispatcher())

    @Test
    fun `toBackupProductImageDataEntity maps ProductImageData to BackupProductImageDataEntity correctly`() = runTest {
        val productId = 1L
        val images = listOf(
            ProductImage(1L, "name1", "image/png", "/path1", "uri1", 100L, "md5-1"),
            ProductImage(2L, "name2", "image/jpeg", "/path2", "uri2", 200L, "md5-2")
        )

        val result = mapper.toBackupProductImageDataEntity(productId, images)

        assertEquals(2, result.size)
        result.forEachIndexed { index, entity ->
            with(images[index]) {
                assertEquals(imageId, entity.imageId)
                assertEquals(productId, entity.productId)
                assertEquals(name, entity.name)
                assertEquals(mimeType, entity.mimeType)
                assertEquals(size, entity.size)
                assertEquals(uriPath, entity.uriPath)
                assertEquals(uriString, entity.uriString)
                assertEquals(md5, entity.md5)
            }
        }
    }

    @Test
    fun `toProductImageData maps BackupProductImageDataEntity to ProductImageData correctly`() = runTest {
        val entities = listOf(
            BackupProductImageDataEntity(
                1L,
                1L,
                "name1",
                "image/png",
                100L,
                "/path1",
                "uri1",
                "md5-1"
            ),
            BackupProductImageDataEntity(
                2L,
                1L,
                "name2",
                "image/jpeg",
                200L,
                "/path2",
                "uri2",
                "md5-2"
            )
        )

        val result = mapper.toProductImageData(entities)

        assertEquals(2, result.size)
        result.forEachIndexed { index, image ->
            with(entities[index]) {
                assertEquals(imageId, image.imageId)
                assertEquals(name, image.name)
                assertEquals(mimeType, image.mimeType)
                assertEquals(size, image.size)
                assertEquals(uriPath, image.uriPath)
                assertEquals(uriString, image.uriString)
                assertEquals(md5, image.md5)
            }
        }
    }
}
