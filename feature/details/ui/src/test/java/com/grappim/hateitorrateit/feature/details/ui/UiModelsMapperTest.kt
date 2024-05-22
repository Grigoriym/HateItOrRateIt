package com.grappim.hateitorrateit.feature.details.ui

import com.grappim.hateitorrateit.testing.domain.getFakeProduct
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class UiModelsMapperTest {

    private val dateTimeUtils: DateTimeUtils = mockk()

    private lateinit var uiModelsMapper: UiModelsMapper

    @Before
    fun setup() {
        uiModelsMapper = UiModelsMapper(
            dateTimeUtils = dateTimeUtils,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun `toProductDetailsUi maps Product to ProductDetailsUi correctly`() = runTest {
        val date = "12.05.25"
        val product = getFakeProduct()
        coEvery { dateTimeUtils.formatToDemonstrate(any()) } returns date

        val expected = createProductDetailsUi().copy(
            id = product.id.toString(),
            name = product.name,
            createdDate = date,
            images = product.images,
            description = product.description,
            productFolderName = product.productFolderName,
            shop = product.shop,
            type = product.type
        )

        val actual = uiModelsMapper.toProductDetailsUi(product)

        assertEquals(expected, actual)
    }

    @Test
    fun `toProductDetailsImageUI maps Product to ProductDetailsImageUi correctly`() = runTest {
        val expected = createProductDetailsImageUi()

        val result = uiModelsMapper.toProductDetailsImageUI(
            getFakeProduct().copy(
                images = expected.images
            )
        )

        assertEquals(expected, result)
    }
}
