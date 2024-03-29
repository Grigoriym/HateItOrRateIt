package com.grappim.hateitorrateit.utils.file

import android.net.Uri
import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.utils.file.images.ImagePersistenceManager
import com.grappim.hateitorrateit.utils.file.images.ImagePersistenceManagerImpl
import com.grappim.hateitorrateit.utils.mappers.ImageDataMapper
import com.grappim.hateitorrateit.utils.models.ImageData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE
)
class ImagePersistenceManagerImplTest {

    private val imageDataMapper: ImageDataMapper = mockk()

    private lateinit var imagePersistenceManager: ImagePersistenceManager

    @Before
    fun setUp() {
        imagePersistenceManager = ImagePersistenceManagerImpl(
            imageDataMapper = imageDataMapper
        )
    }

    @Test
    fun `prepareEditedImagesToPersist should return correct list of ProductImageData`() = runTest {
        val images = listOf(
            ImageData(
                1L,
                uri = Uri.parse("http://example.com/path_temp/image.jpg"),
                name = "image.jpg",
                size = 100L,
                mimeType = "image/jpeg",
                md5 = "abc123",
                isEdit = true
            ),
            ImageData(
                imageId = 2L,
                uri = Uri.parse("http://example.com/path/image2.jpg"),
                name = "image2.jpg",
                size = 200L,
                mimeType = "image/jpeg",
                md5 = "def456",
                isEdit = false
            )
        )

        val expectedProductImages = listOf(
            ProductImageData(
                imageId = 1L,
                name = "image.jpg",
                mimeType = "image/jpeg",
                uriPath = "/path/image.jpg",
                uriString = "http://example.com/path/image.jpg",
                size = 100L,
                md5 = "abc123",
                isEdit = true
            ),
            ProductImageData(
                imageId = 2L,
                name = "image2.jpg",
                mimeType = "image/jpeg",
                uriPath = "/path/image2.jpg",
                uriString = "http://example.com/path/image2.jpg",
                size = 200L,
                md5 = "def456",
                isEdit = false
            )
        )

        images.forEach { image ->
            coEvery {
                imageDataMapper.toProductImageData(image)
            } returns ProductImageData(
                imageId = image.imageId,
                name = image.name,
                mimeType = image.mimeType,
                uriPath = image.uri.path ?: "",
                uriString = image.uri.toString(),
                size = image.size,
                md5 = image.md5,
                isEdit = image.isEdit
            )
        }

        val actual = imagePersistenceManager.prepareEditedImagesToPersist(images)

        assertEquals(expectedProductImages, actual)
        images.forEach { image ->
            coVerify { imageDataMapper.toProductImageData(image) }
        }
    }
}
