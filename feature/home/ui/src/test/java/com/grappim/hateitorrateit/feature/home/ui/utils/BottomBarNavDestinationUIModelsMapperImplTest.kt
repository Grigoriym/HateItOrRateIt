package com.grappim.hateitorrateit.feature.home.ui.utils

import com.grappim.hateitorrateit.feature.home.ui.models.ProductListUI
import com.grappim.hateitorrateit.testing.domain.getFakeProduct
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class BottomBarNavDestinationUIModelsMapperImplTest {

    private val dateTimeUtils: DateTimeUtils = mockk()

    private lateinit var sut: HomeUIModelsMapper

    @Before
    fun setUp() {
        sut = HomeUIModelsMapperImpl(
            dateTimeUtils = dateTimeUtils,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun `toProductUi maps Product to ProductListUI correctly`() = runTest {
        val product = getFakeProduct()
        val date = "123 123"

        coEvery { dateTimeUtils.formatToDemonstrate(any()) } returns date

        val expected = ProductListUI(
            id = product.id.toString(),
            name = product.name,
            createdDate = date,
            previewUriString = product.images.first().uriString,
            productFolderName = product.productFolderName,
            shop = product.shop,
            type = product.type
        )

        val result = sut.toProductUi(product)

        assertEquals(expected, result)
    }
}
