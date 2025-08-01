package com.grappim.hateitorrateit.feature.home.ui.utils

import com.grappim.hateitorrateit.data.analyticsapi.HomeAnalytics
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.data.repoapi.models.Product
import com.grappim.hateitorrateit.feature.home.ui.HomeViewModel
import com.grappim.hateitorrateit.feature.home.ui.models.ProductListUI
import com.grappim.hateitorrateit.testing.core.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BottomBarNavDestinationViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val productsRepository: ProductsRepository = mockk()
    private val homeUIModelsMapper: HomeUIModelsMapper = mockk()
    private val homeAnalytics: HomeAnalytics = mockk()

    private lateinit var viewModel: HomeViewModel

    private val productsList = persistentListOf(
        Product(
            id = 1L,
            name = "name",
            description = "description",
            shop = "shop",
            type = HateRateType.HATE,
            productFolderName = "productFolderName",
            createdDate = OffsetDateTime.now(),
            images = emptyList()
        ),
        Product(
            id = 2L,
            name = "name2",
            description = "description2",
            shop = "shop2",
            type = HateRateType.RATE,
            productFolderName = "productFolderName2",
            createdDate = OffsetDateTime.now(),
            images = emptyList()
        )
    )

    @Before
    fun setup() {
        every {
            productsRepository.getProductsFlow(
                query = any(),
                type = any()
            )
        } returns flowOf(productsList)

        coEvery {
            homeUIModelsMapper.toProductUi(any())
        } returns ProductListUI(
            id = "varius",
            name = "Kathie Anthony",
            shop = "an",
            createdDate = "praesent",
            previewUriString = "neque",
            productFolderName = "Glenna Murphy",
            type = HateRateType.HATE
        )

        viewModel = HomeViewModel(
            productsRepository = productsRepository,
            homeAnalytics = homeAnalytics,
            homeUIModelsMapper = homeUIModelsMapper
        )
    }

    @Test
    fun `on trackScreenStart should call trackHomeScreenStart event`() {
        every { homeAnalytics.trackHomeScreenStart() } just Runs

        viewModel.viewState.value.trackScreenStart()

        verify { homeAnalytics.trackHomeScreenStart() }
    }

    @Test
    fun `on trackOnProductClicked should call trackProductClicked event`() {
        every { homeAnalytics.trackProductClicked() } just Runs

        viewModel.viewState.value.trackOnProductClicked()

        verify { homeAnalytics.trackProductClicked() }
    }

    @Test
    fun `viewState initial state is correct`() = runTest {
        val state = viewModel.viewState.value
        assert(state.products.isNotEmpty())
        assert(state.products.size == 2)

        assert(state.query.isEmpty())
        assert(state.selectedType == null)

        coVerify { homeUIModelsMapper.toProductUi(any()) }
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
