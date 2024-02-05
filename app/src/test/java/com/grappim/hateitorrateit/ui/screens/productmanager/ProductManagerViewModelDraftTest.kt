package com.grappim.hateitorrateit.ui.screens.productmanager

import androidx.lifecycle.SavedStateHandle
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.BackupImagesRepository
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.testing.MainDispatcherRule
import com.grappim.hateitorrateit.ui.screens.DESCRIPTION
import com.grappim.hateitorrateit.ui.screens.NAME
import com.grappim.hateitorrateit.ui.screens.SHOP
import com.grappim.hateitorrateit.ui.screens.TYPE
import com.grappim.hateitorrateit.ui.screens.createCameraTakePictureData
import com.grappim.hateitorrateit.ui.screens.createImageData
import com.grappim.hateitorrateit.ui.screens.createProduct
import com.grappim.hateitorrateit.ui.screens.draftProduct
import com.grappim.hateitorrateit.ui.screens.editProductImages
import com.grappim.hateitorrateit.ui.screens.imageData
import com.grappim.hateitorrateit.ui.screens.uri
import com.grappim.hateitorrateit.utils.FileUtils
import com.grappim.hateitorrateit.utils.mappers.ImageDataMapper
import com.grappim.hateitorrateit.utils.models.ImageData
import com.grappim.hateitorrateit.utils.productmanager.ProductImageManager
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ProductManagerViewModelDraftTest {
    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val fileUtils: FileUtils = mockk()
    private val productsRepository: ProductsRepository = mockk()
    private val dataCleaner: DataCleaner = mockk()
    private val localDataStorage: LocalDataStorage = mockk()
    private val backupImagesRepository: BackupImagesRepository = mockk()
    private val productImageManager: ProductImageManager = mockk()
    private val imageDataMapper: ImageDataMapper = mockk()

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: ProductManagerViewModel

    @Before
    fun setup() {
        savedStateHandle = SavedStateHandle()
        coEvery { productsRepository.addDraftProduct() } returns draftProduct
        every { localDataStorage.typeFlow } returns flowOf(TYPE)

        viewModel = ProductManagerViewModel(
            fileUtils = fileUtils,
            productsRepository = productsRepository,
            dataCleaner = dataCleaner,
            localDataStorage = localDataStorage,
            backupImagesRepository = backupImagesRepository,
            productImageManager = productImageManager,
            imageDataMapper = imageDataMapper,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `on init without draftProduct, draftProduct will be created`() {
        coVerify { productsRepository.addDraftProduct() }

        assertEquals(viewModel.viewState.value.draftProduct, draftProduct)
        assertEquals(viewModel.viewState.value.type, draftProduct.type)
        assertTrue(viewModel.viewState.value.isNewProduct)
    }

    @Test
    fun `with draftProduct, on setDescription sets description`() {
        viewModel.viewState.value.setDescription(DESCRIPTION)
        assertEquals(viewModel.viewState.value.description, DESCRIPTION)
    }

    @Test
    fun `with draftProduct on setName sets name`() {
        viewModel.viewState.value.setName(NAME)
        assertEquals(viewModel.viewState.value.productName, NAME)
    }

    @Test
    fun `with draftProduct on setShop sets shop`() {
        viewModel.viewState.value.setShop(SHOP)
        assertEquals(viewModel.viewState.value.shop, SHOP)
    }

    @Test
    fun `with draftProduct, on onQuit calls dataCleaner`() {
        coEvery { dataCleaner.clearProductData(any(), any()) } just Runs

        assertEquals(viewModel.viewState.value.quitStatus, QuitStatus.Initial)

        viewModel.viewState.value.onQuit()

        assertEquals(viewModel.viewState.value.quitStatus, QuitStatus.Finish)

        coVerify {
            dataCleaner.clearProductData(
                viewModel.viewState.value.draftProduct!!.id,
                viewModel.viewState.value.draftProduct!!.productFolderName,
            )
        }
    }

    @Test
    fun `with draftProduct, on onProductDone, new product is created`() {
        coEvery { productsRepository.addProduct(any()) } just Runs
        coEvery { imageDataMapper.toProductImageDataList(any()) } returns editProductImages
        every { fileUtils.getFileUriFromGalleryUri(any(), any(), any()) } returns imageData

        viewModel.viewState.value.setName(NAME)
        viewModel.viewState.value.setDescription(DESCRIPTION)
        viewModel.viewState.value.setShop(SHOP)
        viewModel.viewState.value.onAddImageFromGalleryClicked(uri)

        viewModel.viewState.value.onProductDone()

        coVerify { productsRepository.addProduct(createProduct) }

        assertTrue(viewModel.viewState.value.productSaved)
    }

    @Test
    fun `with draftProduct, on onTypeClicked, type is changed`() {
        viewModel.viewState.value.onTypeClicked(HateRateType.HATE)

        assertEquals(viewModel.viewState.value.type, HateRateType.HATE)
    }

    @Test
    fun `with draftProduct, on onShowAlertDialog true, showAlertDialog value changes to true`() {
        viewModel.viewState.value.onShowAlertDialog(true)

        assertTrue(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun `with draftProduct, on onForceQuit, forceQUit should be true`() {
        viewModel.viewState.value.onForceQuit()

        assertTrue(viewModel.viewState.value.forceQuit)
    }

    @Test
    fun `with draftProduct, onRemoveImageClicked with success delete, should delete image`() {
        every { fileUtils.deleteFile(uri = any()) } returns true

        val firstImage = createImageData()
        val imageToDelete = createImageData()

        prepareImage(firstImage)
        prepareImage(imageToDelete)

        viewModel.viewState.value.onRemoveImageClicked(imageToDelete)

        verify { fileUtils.deleteFile(imageToDelete.uri) }

        assertTrue(viewModel.viewState.value.images.size == 1)
        assertEquals(viewModel.viewState.value.images.first(), firstImage)
    }

    @Test
    fun `with draftProduct, onAddImageFromGalleryClicked should add image`() {
        val imageData = createImageData()
        every { fileUtils.getFileUriFromGalleryUri(any(), any(), any()) } returns imageData

        assertTrue(viewModel.viewState.value.images.isEmpty())

        viewModel.viewState.value.onAddImageFromGalleryClicked(imageData.uri)

        verify {
            fileUtils.getFileUriFromGalleryUri(
                uri = imageData.uri,
                folderName = getProductFolderName(),
                isEdit = false,
            )
        }

        assertTrue(viewModel.viewState.value.images.size == 1)
    }

    @Test
    fun `with draftProduct, onAddCameraPictureClicked should add image`() {
        val cameraTakePictureData = createCameraTakePictureData()
        val imageData = createImageData(
            newUri = cameraTakePictureData.uri
        )

        every { fileUtils.getFileDataFromCameraPicture(any(), any()) } returns imageData

        assertTrue(viewModel.viewState.value.images.isEmpty())

        viewModel.viewState.value.onAddCameraPictureClicked(cameraTakePictureData)

        verify {
            fileUtils.getFileDataFromCameraPicture(
                cameraTakePictureData = cameraTakePictureData,
                isEdit = false,
            )
        }

        assertTrue(viewModel.viewState.value.images.size == 1)
    }

    @Test
    fun `with draftProduct, getCameraImageFileUri should return correct CameraTakePictureData`() {
        val cameraTakePictureData = createCameraTakePictureData()

        every { fileUtils.getFileUriForTakePicture(any(), any()) } returns cameraTakePictureData

        val cameraData = viewModel.viewState.value.getCameraImageFileUri.invoke()

        verify {
            fileUtils.getFileUriForTakePicture(
                folderName = getProductFolderName(),
                isEdit = false,
            )
        }

        assertEquals(cameraData, cameraTakePictureData)
    }

    private fun prepareImage(imageData: ImageData) {
        every {
            fileUtils.getFileUriFromGalleryUri(any(), any(), any())
        } returns imageData
        viewModel.viewState.value.onAddImageFromGalleryClicked(imageData.uri)
    }

    private fun getProductFolderName(): String =
        requireNotNull(viewModel.viewState.value.draftProduct).productFolderName
}
