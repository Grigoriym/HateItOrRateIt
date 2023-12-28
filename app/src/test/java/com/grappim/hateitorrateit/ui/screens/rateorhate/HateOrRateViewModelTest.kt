package com.grappim.hateitorrateit.ui.screens.rateorhate

import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.domain.DraftProduct
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.testing.MainDispatcherRule
import com.grappim.hateitorrateit.utils.FileUtils
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val TYPE = HateRateType.RATE

class HateOrRateViewModelTest {
    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val fileUtils: FileUtils = mockk()
    private val productsRepository: ProductsRepository = mockk()
    private val dataCleaner: DataCleaner = mockk()
    private val localDataStorage: LocalDataStorage = mockk()

    private lateinit var viewModel: HateOrRateViewModel

    private val draftProduct = DraftProduct(
        id = 6061,
        date = OffsetDateTime.now(),
        folderName = "Ellis Williamson",
        type = TYPE,
    )

    @Before
    fun setup() {
        every { localDataStorage.typeFlow } returns flowOf(TYPE)
        coEvery { productsRepository.addDraftProduct() } returns draftProduct

        viewModel = HateOrRateViewModel(
            fileUtils = fileUtils,
            productsRepository = productsRepository,
            dataCleaner = dataCleaner,
            localDataStorage = localDataStorage,
        )
    }

    @Test
    fun `on init draftProduct is created`() {
        coVerify { productsRepository.addDraftProduct() }

        assertEquals(viewModel.viewState.value.draftProduct, draftProduct)
        assertEquals(viewModel.viewState.value.type, draftProduct.type)
    }

    @Test
    fun `on setDescription sets description`() {
        viewModel.viewState.value.setDescription("test")
        assertEquals(viewModel.viewState.value.description, "test")
    }

    @Test
    fun `on setName sets name`() {
        viewModel.viewState.value.setName("test")
        assertEquals(viewModel.viewState.value.productName, "test")
    }

    @Test
    fun `on setShop sets shop`() {
        viewModel.viewState.value.setShop("test")
        assertEquals(viewModel.viewState.value.shop, "test")
    }

    @Test
    fun `on removeData calls dataCleaner`() {
        coEvery { dataCleaner.clearProductData(any()) } just Runs

        viewModel.viewState.value.removeData()

        coVerify { dataCleaner.clearProductData(any()) }
    }

    @Test
    fun `on saveData, product is created`() {
        coEvery { productsRepository.addProduct(any()) } just Runs

        viewModel.viewState.value.saveData()

        coVerify { productsRepository.addProduct(any()) }

        assertTrue(viewModel.viewState.value.isCreated)
    }

    @Test
    fun `on onTypeClicked, type is changed`() {
        viewModel.viewState.value.onTypeClicked(HateRateType.HATE)

        assertEquals(viewModel.viewState.value.type, HateRateType.HATE)
    }

    @Test
    fun `on onShowAlertDialog true, showAlertDialog value changes to true`() {
        viewModel.viewState.value.onShowAlertDialog(true)

        assertTrue(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun `on onForceQuit, forceQUit should be true`() {
        viewModel.viewState.value.onForceQuit()

        assertTrue(viewModel.viewState.value.forceQuit)
    }

}
