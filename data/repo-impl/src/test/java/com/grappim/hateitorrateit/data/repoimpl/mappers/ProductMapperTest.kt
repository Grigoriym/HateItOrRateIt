package com.grappim.hateitorrateit.data.repoimpl.mappers

import com.grappim.hateitorrateit.data.repoimpl.ID
import com.grappim.hateitorrateit.data.repoimpl.getCreateProduct
import com.grappim.hateitorrateit.data.repoimpl.getListOfEmptyFileData
import com.grappim.hateitorrateit.data.repoimpl.getProduct
import com.grappim.hateitorrateit.data.repoimpl.getProductEntity
import com.grappim.hateitorrateit.data.repoimpl.getProductImageData
import com.grappim.hateitorrateit.data.repoimpl.getProductImageDataEntity
import com.grappim.hateitorrateit.data.repoimpl.getProductImageDataEntityList
import com.grappim.hateitorrateit.data.repoimpl.getProductImageDataList
import com.grappim.hateitorrateit.data.repoimpl.getProductWithImagesEntity
import com.grappim.hateitorrateit.data.repoimpl.getProductWithImagesEntityList
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ProductMapperTest {

    private val productMapper = ProductMapper(
        ioDispatcher = UnconfinedTestDispatcher()
    )

    @Test
    fun `toProduct should return correct product`() = runTest {
        val actual = productMapper.toProduct(getProductWithImagesEntity())
        val expected = getProduct()
        assertEquals(expected, actual)
    }

    @Test
    fun `toProductImageDataEntityList should return correct list`() = runTest {
        val actual = productMapper.toProductImageDataEntityList(
            productId = ID,
            images = getProductImageDataList()
        )
        val expected = getProductImageDataEntityList()
        assertEquals(expected, actual)
    }

    @Test
    fun `toProductImageDataEntity should return correct entity`() = runTest {
        val actual = productMapper.toProductImageDataEntity(
            productId = ID,
            productImage = getProductImageData()
        )
        val expected = getProductImageDataEntity()
        assertEquals(expected, actual)
    }

    @Test
    fun `toEmptyFileDataList should return correct list`() = runTest {
        val actual = productMapper.toEmptyFileList(
            list = getProductWithImagesEntityList()
        )
        val expected = getListOfEmptyFileData()
        assertEquals(expected, actual)
    }

    @Test
    fun `toProductEntity should return correct entity`() = runTest {
        val actual = productMapper.toProductEntity(
            createProduct = getCreateProduct()
        )
        val expected = getProductEntity()
        assertEquals(expected, actual)
    }

    @Test
    fun `toProductImageDataEntityList should return correct list of entities`() = runTest {
        val actual = productMapper.toProductImageDataEntityList(
            productId = ID,
            images = getProductImageDataList()
        )
        val expected = getProductImageDataEntityList()
        assertEquals(expected, actual)
    }

    @Test
    fun `toProduct should return correct product with null files`() = runTest {
        val actual = productMapper.toProduct(
            productEntity = getProductEntity(),
            images = null
        )
        val expected = getProduct(
            filesUri = emptyList()
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `toProductImageData should return correct ProductImageData`() = runTest {
        val actual = productMapper.toProductImageData(getProductImageDataEntity())
        val expected = getProductImageData()
        assertEquals(expected, actual)
    }
}
