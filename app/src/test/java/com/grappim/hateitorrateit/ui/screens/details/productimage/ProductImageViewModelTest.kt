package com.grappim.hateitorrateit.ui.screens.details.productimage

import androidx.lifecycle.SavedStateHandle
import com.grappim.hateitorrateit.core.navigation.RootNavDestinations
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.model.UiModelsMapper
import com.grappim.hateitorrateit.model.createProductDetailsImageUi
import com.grappim.hateitorrateit.ui.screens.PRODUCT_ID
import com.grappim.hateitorrateit.ui.screens.createEditProduct
import com.grappim.hateitorrateit.ui.screens.editProductImages
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

private const val INDEX = 0

@RunWith(RobolectricTestRunner::class)
class ProductImageViewModelTest {

    private val productsRepository: ProductsRepository = mockk()
    private val uiModelsMapper: UiModelsMapper = mockk()

    private lateinit var vm: ProductImageViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle()
        savedStateHandle[RootNavDestinations.DetailsImage.KEY_PRODUCT_ID] = PRODUCT_ID.toString()
        savedStateHandle[RootNavDestinations.DetailsImage.KEY_INDEX] = INDEX
    }

    @Test
    fun `on init should update state with correct data`() = runTest {
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

        assertEquals(vm.viewState.value.uri, productUi.images[INDEX].uriString)
        assertContentEquals(vm.viewState.value.images, productUi.images)
    }
}
