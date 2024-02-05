package com.grappim.hateitorrateit.ui.screens.home

import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.domain.Product
import com.grappim.hateitorrateit.model.ProductListUI
import com.grappim.hateitorrateit.model.UiModelsMapper
import com.grappim.hateitorrateit.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HomeScreenViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val productsRepository: ProductsRepository = mockk()

    private val uiModelsMapper: UiModelsMapper = mockk()

    private lateinit var viewModel: HomeScreenViewModel

    private val productsList = listOf(
        Product(
            id = 1L,
            name = "name",
            description = "description",
            shop = "shop",
            type = HateRateType.HATE,
            productFolderName = "productFolderName",
            createdDate = OffsetDateTime.now(),
            images = emptyList(),
        ),
        Product(
            id = 2L,
            name = "name2",
            description = "description2",
            shop = "shop2",
            type = HateRateType.RATE,
            productFolderName = "productFolderName2",
            createdDate = OffsetDateTime.now(),
            images = emptyList(),
        ),
    )

    @Before
    fun setup() {
        every {
            productsRepository.getProductsFlow(
                query = any(),
                type = any(),
            )
        } returns flowOf(productsList)

        coEvery {
            uiModelsMapper.toProductUi(any())
        } returns ProductListUI(
            id = "varius",
            name = "Kathie Anthony",
            shop = "an",
            createdDate = "praesent",
            previewUriString = "neque",
            productFolderName = "Glenna Murphy",
            type = HateRateType.HATE,
        )

        viewModel = HomeScreenViewModel(
            productsRepository = productsRepository,
            uiModelsMapper = uiModelsMapper,
        )
    }

    @Test
    fun `viewState initial state is correct`() = runTest {
        val state = viewModel.viewState.value
        assert(state.products.isNotEmpty())
        assert(state.products.size == 2)

        assert(state.query.isEmpty())
        assert(state.selectedType == null)

        verify { productsRepository.getProductsFlow(any(),any()) }
        coVerify { uiModelsMapper.toProductUi(any()) }
    }

    @Test
    fun `onFilterSelected with different type, should change the type to a new one`() = runTest {
        assertNull(viewModel.viewState.value.selectedType)

        viewModel.viewState.value.onFilterSelected(HateRateType.HATE)

        assertEquals(HateRateType.HATE, viewModel.viewState.value.selectedType)
    }

    @Test
    fun `onFilterSelected with same type, should change the type to null`() = runTest {
        viewModel.viewState.value.onFilterSelected(HateRateType.HATE)
        assertEquals(HateRateType.HATE, viewModel.viewState.value.selectedType)

        viewModel.viewState.value.onFilterSelected(HateRateType.HATE)
        assertNull(viewModel.viewState.value.selectedType)
    }

    @Test
    fun `onSearchQueryChanged, should change the query`() = runTest {
        viewModel.viewState.value.onSearchQueryChanged("query")
        assertEquals("query", viewModel.viewState.value.query)
    }

    @Test
    fun `clearQuery, should clear the query`() = runTest {
        viewModel.viewState.value.onSearchQueryChanged("query")
        assertEquals("query", viewModel.viewState.value.query)

        viewModel.viewState.value.onClearQueryClicked()
        assertEquals("", viewModel.viewState.value.query)
    }
}
