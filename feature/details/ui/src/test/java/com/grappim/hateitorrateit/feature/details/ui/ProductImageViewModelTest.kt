package com.grappim.hateitorrateit.feature.details.ui

import androidx.lifecycle.SavedStateHandle
import com.grappim.hateitorrateit.core.navigation.NavDestinations
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.feature.details.ui.mappers.UiModelsMapper
import com.grappim.hateitorrateit.feature.details.ui.productimage.ProductImageViewModel
import com.grappim.hateitorrateit.testing.domain.PRODUCT_ID
import com.grappim.hateitorrateit.testing.domain.createEditProduct
import com.grappim.hateitorrateit.testing.domain.editProductImages
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@RunWith(RobolectricTestRunner::class)
class ProductImageViewModelTest {

    private val index = 0

    private val productsRepository: ProductsRepository = mockk()
    private val uiModelsMapper: UiModelsMapper = mockk()

    private lateinit var vm: ProductImageViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle()
    }

    @Test
    fun `on init should update state with correct data`() = runTest {
        savedStateHandle[NavDestinations.DetailsImage.KEY_PRODUCT_ID] = PRODUCT_ID.toString()
        savedStateHandle[NavDestinations.DetailsImage.KEY_INDEX] = index

        val product = createEditProduct(editProductImages)
        val productUi = createProductDetailsImageUi()

        coEvery { productsRepository.getProductById(any()) } returns product
        coEvery { uiModelsMapper.toProductDetailsImageUI(any()) } returns productUi

        vm = ProductImageViewModel(
            productsRepository = productsRepository,
            uiModelsMapper = uiModelsMapper,
            savedStateHandle = savedStateHandle
        )

        coVerify { productsRepository.getProductById(PRODUCT_ID) }
        coVerify { uiModelsMapper.toProductDetailsImageUI(product) }

        assertEquals(vm.viewState.value.uri, productUi.images[index].uriString)
    }

    @Test
    fun `on init with productId being null, should throw an error`() {
        savedStateHandle[NavDestinations.DetailsImage.KEY_PRODUCT_ID] = null

        assertFailsWith<IllegalStateException> {
            ProductImageViewModel(
                productsRepository = productsRepository,
                uiModelsMapper = uiModelsMapper,
                savedStateHandle = savedStateHandle
            )
        }
    }

    @Test
    fun `on init with index being null, should throw an error`() {
        savedStateHandle[NavDestinations.DetailsImage.KEY_INDEX] = null

        assertFailsWith<IllegalStateException> {
            ProductImageViewModel(
                productsRepository = productsRepository,
                uiModelsMapper = uiModelsMapper,
                savedStateHandle = savedStateHandle
            )
        }
    }
}
