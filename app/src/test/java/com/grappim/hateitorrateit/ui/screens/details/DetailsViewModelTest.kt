package com.grappim.hateitorrateit.ui.screens.details

import androidx.lifecycle.SavedStateHandle
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.domain.Product
import com.grappim.hateitorrateit.model.ProductDetailsUi
import com.grappim.hateitorrateit.model.UiModelsMapper
import com.grappim.hateitorrateit.testing.MainDispatcherRule
import com.grappim.hateitorrateit.utils.FileUtils
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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

class DetailsViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val productsRepository: ProductsRepository = mockk()
    private val uiModelsMapper: UiModelsMapper = mockk()
    private val dataCleaner: DataCleaner = mockk()
    private val fileUtils: FileUtils = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    private lateinit var viewModel: DetailsViewModel

    private val product = Product(
        id = PRODUCT_ID,
        name = "name",
        description = "description",
        shop = "shop",
        type = HateRateType.HATE,
        productFolderName = "productFolderName",
        createdDate = OffsetDateTime.now(),
        filesUri = emptyList(),
    )

    private val productUi = ProductDetailsUi(
        id = PRODUCT_ID.toString(),
        name = "Cameron Thomas",
        createdDate = "reque",
        filesUri = listOf(),
        productFolderName = "Antonio Hopper",
        description = "felis",
        shop = "fabellas",
        type = HateRateType.HATE,
    )

    @Before
    fun setup() {
        every {
            savedStateHandle.get<Long>(any())
        } returns 1L
        coEvery {
            productsRepository.getProductById(any())
        } returns product
        coEvery { uiModelsMapper.toProductDetailsUi(any()) } returns productUi

        viewModel = DetailsViewModel(
            productsRepository = productsRepository,
            uiModelsMapper = uiModelsMapper,
            dataCleaner = dataCleaner,
            fileUtils = fileUtils,
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun `on onDeleteProductConfirm, clearProductData should be called and productDeleted is true`() {
        coEvery { dataCleaner.clearProductData(any(), any()) } just Runs

        viewModel.viewState.value.onDeleteProductConfirm()

        assertTrue(viewModel.viewState.value.isLoading)
        assertEquals(viewModel.viewState.value.id, PRODUCT_ID.toString())
        coVerify { dataCleaner.clearProductData(any(), any()) }
        assertTrue(viewModel.viewState.value.productDeleted)
    }

    @Test
    fun `onShowAlertDialog called showAlertDialog should be true`() {
        assertFalse(viewModel.viewState.value.showAlertDialog)
        viewModel.viewState.value.onShowAlertDialog(true)
        assertTrue(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun `onDeleteProduct called showAlertDialog should be true`() {
        viewModel.viewState.value.onShowAlertDialog(false)
        assertFalse(viewModel.viewState.value.showAlertDialog)

        viewModel.viewState.value.onDeleteProduct()

        assertTrue(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun `on toggleEditMode, toEdit fields should respectively be as non-toEdit fields, isEdit should be true`() {
        assertFalse(viewModel.viewState.value.isEdit)

        viewModel.viewState.value.onToggleEditMode()

        assertTrue(viewModel.viewState.value.isEdit)
        assertEquals(viewModel.viewState.value.nameToEdit, viewModel.viewState.value.name)
        assertEquals(
            viewModel.viewState.value.descriptionToEdit,
            viewModel.viewState.value.description
        )
        assertEquals(viewModel.viewState.value.shopToEdit, viewModel.viewState.value.shop)
        assertEquals(viewModel.viewState.value.typeToEdit, viewModel.viewState.value.type)
    }

    @Test
    fun `onEditSubmit, with typeToEdit being not null, should call updateProduct`() {
        coEvery { productsRepository.updateProduct(any(), any(), any(), any(), any()) } just Runs
        viewModel.viewState.value.onToggleEditMode()

        viewModel.viewState.value.onSubmitChanges()
        coVerify {
            productsRepository.updateProduct(
                id = PRODUCT_ID,
                name = viewModel.viewState.value.nameToEdit,
                description = viewModel.viewState.value.descriptionToEdit,
                shop = viewModel.viewState.value.shopToEdit,
                type = viewModel.viewState.value.typeToEdit!!,
            )
        }
    }

    @Test
    fun `setName, should update nameToEdit`() {
        viewModel.viewState.value.onSetName("newName")
        assertEquals("newName", viewModel.viewState.value.nameToEdit)
    }

    @Test
    fun `setShop, should update shopToEdit`() {
        viewModel.viewState.value.onSetShop("newShop")
        assertEquals("newShop", viewModel.viewState.value.shopToEdit)
    }

    @Test
    fun `setDescription, should update descriptionToEdit`() {
        viewModel.viewState.value.onSetDescription("newDescription")
        assertEquals("newDescription", viewModel.viewState.value.descriptionToEdit)
    }

    @Test
    fun `setType, should update typeToEdit`() {
        viewModel.viewState.value.onToggleEditMode()

        viewModel.viewState.value.onSetType(HateRateType.HATE)
        assertEquals(HateRateType.HATE, viewModel.viewState.value.typeToEdit)
    }
}
