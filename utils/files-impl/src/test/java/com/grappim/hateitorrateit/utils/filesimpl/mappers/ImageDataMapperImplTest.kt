package com.grappim.hateitorrateit.utils.filesimpl.mappers

import com.grappim.hateitorrateit.data.repoapi.models.ProductImage
import com.grappim.hateitorrateit.testing.core.getRandomUri
import com.grappim.hateitorrateit.testing.domain.getRandomLong
import com.grappim.hateitorrateit.testing.domain.getRandomString
import com.grappim.hateitorrateit.utils.filesapi.mappers.ImageDataMapper
import com.grappim.hateitorrateit.utils.filesapi.models.ProductImageUIData
import com.grappim.hateitorrateit.utils.filesapi.uri.UriParser
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

    private lateinit var sut: ImageDataMapper

    @Before
    fun setup() {
        sut = ImageDataMapperImpl(
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

        val expected = ProductImage(
            imageId = id,
            name = name,
            mimeType = mimeType,
            uriPath = uri.path ?: "",
            uriString = uri.toString(),
            size = size,
            md5 = md5,
            isEdit = false
        )

        val productImageUIData = ProductImageUIData(
            imageId = id,
            uri = uri,
            name = name,
            size = size,
            mimeType = mimeType,
            md5 = md5,
            isEdit = false
        )

        val actual = sut.toProductImageData(productImageUIData)

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

        val productImage = ProductImage(
            imageId = id,
            name = name,
            mimeType = mimeType,
            uriPath = uri.path ?: "",
            uriString = uri.toString(),
            size = size,
            md5 = md5,
            isEdit = false
        )

        val expected = ProductImageUIData(
            imageId = id,
            uri = uri,
            name = name,
            size = size,
            mimeType = mimeType,
            md5 = md5,
            isEdit = false
        )

        val actual = sut.toImageData(productImage)

        assertEquals(expected, actual)

        verify { uriParser.parse(productImage.uriString) }
    }

    @Test
    fun `toProductImageDataList should return correct List of ProductImageData`() = runTest {
        val productImageUIDataLists = listOf(
            ProductImageUIData(
                getRandomLong(),
                getRandomUri(),
                getRandomString(),
                getRandomLong(),
                getRandomString(),
                getRandomString(),
                false
            ),
            ProductImageUIData(
                getRandomLong(),
                getRandomUri(),
                getRandomString(),
                getRandomLong(),
                getRandomString(),
                getRandomString(),
                true
            )
        )

        val expectedList = productImageUIDataLists.map { imageData ->
            ProductImage(
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

        val actualList = sut.toProductImageDataList(productImageUIDataLists)

        assertEquals(expectedList, actualList)
    }

    @Test
    fun `toImageDataList should return correct List of ImageData`() = runTest {
        val mockedUri = getRandomUri()
        every { uriParser.parse(any()) } returns mockedUri

        val productImageLists = listOf(
            ProductImage(
                getRandomLong(),
                getRandomString(),
                getRandomString(),
                mockedUri.path ?: "",
                mockedUri.toString(),
                getRandomLong(),
                getRandomString(),
                false
            ),
            ProductImage(
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

        val expectedList = productImageLists.map { productImageData ->
            ProductImageUIData(
                imageId = productImageData.imageId,
                uri = mockedUri,
                name = productImageData.name,
                size = productImageData.size,
                mimeType = productImageData.mimeType,
                md5 = productImageData.md5,
                isEdit = productImageData.isEdit
            )
        }

        val actualList = sut.toImageDataList(productImageLists)

        assertEquals(expectedList, actualList)

        productImageLists.forEach { productImageData ->
            verify { uriParser.parse(productImageData.uriString) }
        }
    }
}
