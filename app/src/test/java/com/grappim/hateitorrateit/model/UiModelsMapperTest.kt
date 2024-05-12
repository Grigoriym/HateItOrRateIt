package com.grappim.hateitorrateit.model

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
    fun `toProductUi maps Product to ProductListUI correctly`() = runTest {
        val product = createProduct()

        coEvery { dateTimeUtils.formatToDemonstrate(any()) } returns DATE

        val expected = createProductListUI()

        val result = uiModelsMapper.toProductUi(product)

        assertEquals(expected, result)
    }

    @Test
    fun `toProductDetailsUi maps Product to ProductDetailsUi correctly`() = runTest {
        val product = createProduct()
        coEvery { dateTimeUtils.formatToDemonstrate(any()) } returns DATE

        val expected = createProductDetailsUi()

        val result = uiModelsMapper.toProductDetailsUi(product)

        assertEquals(expected, result)
    }

    @Test
    fun `toProductDetailsImageUI maps Product to ProductDetailsImageUi correctly`() = runTest {
        val expected = createProductDetailsImageUi()

        val result = uiModelsMapper.toProductDetailsImageUI(createProduct())

        assertEquals(expected, result)
    }
}
