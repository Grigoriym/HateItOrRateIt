package com.grappim.hateitorrateit.utils.mappers

import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.testing.getRandomLong
import com.grappim.hateitorrateit.testing.getRandomString
import com.grappim.hateitorrateit.testing.getRandomUri
import com.grappim.hateitorrateit.utils.UriParser
import com.grappim.hateitorrateit.utils.models.ImageData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ImageDataMapperImplTest {

    private val uriParser: UriParser = mockk()

    private lateinit var imageDataMapper: ImageDataMapper

    @Before
    fun setup() {
        imageDataMapper = ImageDataMapperImpl(
            uriParser = uriParser,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun `toProductImageData should return correct ProductImageData`() = runTest {
        val uri = getRandomUri()
        val id = getRandomLong()
        val name = getRandomString()
        val size = getRandomLong()
        val md5 = getRandomString()
        val mimeType = getRandomString()

        val expected = ProductImageData(
            imageId = id,
            name = name,
            mimeType = mimeType,
            uriPath = uri.path ?: "",
            uriString = uri.toString(),
            size = size,
            md5 = md5,
            isEdit = false
        )

        val imageData = ImageData(
            imageId = id,
            uri = uri,
            name = name,
            size = size,
            mimeType = mimeType,
            md5 = md5,
            isEdit = false
        )

        val actual = imageDataMapper.toProductImageData(imageData)

        assertEquals(expected, actual)
    }

    @Test
    fun `toImageData should return correct ImageData`() = runTest {
        val uri = getRandomUri()
        val id = getRandomLong()
        val name = getRandomString()
        val size = getRandomLong()
        val md5 = getRandomString()
        val mimeType = getRandomString()

        every { uriParser.parse(any()) } returns uri

        val productImageData = ProductImageData(
            imageId = id,
            name = name,
            mimeType = mimeType,
            uriPath = uri.path ?: "",
            uriString = uri.toString(),
            size = size,
            md5 = md5,
            isEdit = false
        )

        val expected = ImageData(
            imageId = id,
            uri = uri,
            name = name,
            size = size,
            mimeType = mimeType,
            md5 = md5,
            isEdit = false
        )

        val actual = imageDataMapper.toImageData(productImageData)

        assertEquals(expected, actual)

        verify { uriParser.parse(productImageData.uriString) }
    }

    @Test
    fun `toProductImageDataList should return correct List of ProductImageData`() = runTest {
        val imageDataList = listOf(
            ImageData(
                getRandomLong(),
                getRandomUri(),
                getRandomString(),
                getRandomLong(),
                getRandomString(),
                getRandomString(),
                false
            ),
            ImageData(
                getRandomLong(),
                getRandomUri(),
                getRandomString(),
                getRandomLong(),
                getRandomString(),
                getRandomString(),
                true
            )
        )

        val expectedList = imageDataList.map { imageData ->
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

        val actualList = imageDataMapper.toProductImageDataList(imageDataList)

        assertEquals(expectedList, actualList)
    }

    @Test
    fun `toImageDataList should return correct List of ImageData`() = runTest {
        val mockedUri = getRandomUri()
        every { uriParser.parse(any()) } returns mockedUri

        val productImageDataList = listOf(
            ProductImageData(
                getRandomLong(),
                getRandomString(),
                getRandomString(),
                mockedUri.path ?: "",
                mockedUri.toString(),
                getRandomLong(),
                getRandomString(),
                false
            ),
            ProductImageData(
                getRandomLong(),
                getRandomString(),
                getRandomString(),
                mockedUri.path ?: "",
                mockedUri.toString(),
                getRandomLong(),
                getRandomString(),
                true
            )
        )

        val expectedList = productImageDataList.map { productImageData ->
            ImageData(
                imageId = productImageData.imageId,
                uri = mockedUri,
                name = productImageData.name,
                size = productImageData.size,
                mimeType = productImageData.mimeType,
                md5 = productImageData.md5,
                isEdit = productImageData.isEdit
            )
        }

        val actualList = imageDataMapper.toImageDataList(productImageDataList)

        assertEquals(expectedList, actualList)

        productImageDataList.forEach { productImageData ->
            verify { uriParser.parse(productImageData.uriString) }
        }
    }
}
