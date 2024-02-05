package com.grappim.hateitorrateit.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import com.grappim.hateitorrateit.core.navigation.RootNavDestinations
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.domain.Product
import com.grappim.hateitorrateit.model.ProductDetailsUi
import com.grappim.hateitorrateit.model.UiModelsMapper
import com.grappim.hateitorrateit.testing.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val PRODUCT_ID = 1L
private const val PRODUCT_FOLDER_NAME = "productFolderNameTest"
private const val PRODUCT_NAME = "productName"

class DetailsViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val productsRepository: ProductsRepository = mockk()
    private val uiModelsMapper: UiModelsMapper = mockk()
    private val dataCleaner: DataCleaner = mockk()
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: DetailsViewModel

    private val product = Product(
        id = PRODUCT_ID,
        name = PRODUCT_NAME,
        description = "description",
        shop = "shop",
        type = HateRateType.HATE,
        productFolderName = PRODUCT_FOLDER_NAME,
        createdDate = OffsetDateTime.now(),
        images = emptyList(),
    )

    private val productUi = ProductDetailsUi(
        id = PRODUCT_ID.toString(),
        name = PRODUCT_NAME,
        createdDate = "reque",
        filesUri = listOf(),
        productFolderName = PRODUCT_FOLDER_NAME,
        description = "felis",
        shop = "fabellas",
        type = HateRateType.HATE,
    )

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle().apply {
            this[RootNavDestinations.Details.KEY] = PRODUCT_ID
        }
        coEvery {
            productsRepository.getProductById(any())
        } returns product
        coEvery { uiModelsMapper.toProductDetailsUi(any()) } returns productUi

        viewModel = DetailsViewModel(
            productsRepository = productsRepository,
            uiModelsMapper = uiModelsMapper,
            dataCleaner = dataCleaner,
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun `on onDeleteProductConfirm, clearProductData should be called and productDeleted is true`() {
        coEvery { dataCleaner.clearProductData(any(), any()) } just Runs

        viewModel.viewState.value.onDeleteProductConfirm()

        assertTrue(viewModel.viewState.value.isLoading)
        assertEquals(viewModel.viewState.value.productId, PRODUCT_ID.toString())
        coVerify {
            dataCleaner.clearProductData(
                productId = PRODUCT_ID,
                productFolderName = PRODUCT_FOLDER_NAME
            )
        }
        assertTrue(viewModel.viewState.value.productDeleted)
    }

    @Test
    fun `onShowAlertDialog, showAlertDialog should be true`() {
        assertFalse(viewModel.viewState.value.showAlertDialog)
        viewModel.viewState.value.onShowAlertDialog(true)
        assertTrue(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun `onDeleteProduct, showAlertDialog should be true`() {
        assertFalse(viewModel.viewState.value.showAlertDialog)

        viewModel.viewState.value.onDeleteProduct()

        assertTrue(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun `on updateProduct should getProduct`() {
        viewModel.viewState.value.updateProduct()

        coVerify {
            productsRepository.getProductById(PRODUCT_ID)
        }
        coVerify {
            uiModelsMapper.toProductDetailsUi(product)
        }

        val state = viewModel.viewState.value

        assertEquals(state.productId, PRODUCT_ID.toString())
        assertEquals(state.name, PRODUCT_NAME)
        assertFalse(state.isLoading)
        assertEquals(state.productFolderName, PRODUCT_FOLDER_NAME)
    }
}
