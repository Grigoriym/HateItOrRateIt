package com.grappim.hateitorrateit.ui.screens.productmanager

import androidx.lifecycle.SavedStateHandle
import com.grappim.hateitorrateit.core.navigation.RootNavDestinations
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.BackupImagesRepository
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.testing.MainDispatcherRule
import com.grappim.hateitorrateit.ui.screens.DESCRIPTION
import com.grappim.hateitorrateit.ui.screens.NAME
import com.grappim.hateitorrateit.ui.screens.PRODUCT_ID
import com.grappim.hateitorrateit.ui.screens.SHOP
import com.grappim.hateitorrateit.ui.screens.TYPE
import com.grappim.hateitorrateit.ui.screens.createImageData
import com.grappim.hateitorrateit.ui.screens.editProduct
import com.grappim.hateitorrateit.ui.screens.editProductImages
import com.grappim.hateitorrateit.ui.screens.createCameraTakePictureData
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
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class ProductManagerViewModelEditTest {
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
        every { localDataStorage.typeFlow } returns flowOf(TYPE)

        savedStateHandle[RootNavDestinations.ProductManager.KEY_EDIT_PRODUCT_ID] = PRODUCT_ID.toString()

        coEvery { productsRepository.getProductById(any()) } returns editProduct
        coEvery { productImageManager.copyToBackupFolder(any()) } just Runs
        coEvery { backupImagesRepository.insertImages(any(), any()) } just Runs
        coEvery { imageDataMapper.toImageDataList(any()) } returns emptyList()

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

    @After
    fun clear() {
        savedStateHandle.remove<Long>(RootNavDestinations.ProductManager.KEY_EDIT_PRODUCT_ID)
    }

    @Test
    fun `on init with editProduct, edit product will be prepared`() {
        coVerify { productsRepository.getProductById(PRODUCT_ID) }
        coVerify { productImageManager.copyToBackupFolder(editProduct.productFolderName) }
        coVerify { backupImagesRepository.insertImages(PRODUCT_ID, editProduct.images) }
        coVerify { imageDataMapper.toImageDataList(editProduct.images) }

        assertNull(viewModel.viewState.value.draftProduct)
        assertEquals(viewModel.viewState.value.editProduct, editProduct)
        assertEquals(viewModel.viewState.value.type, editProduct.type)
        assertEquals(viewModel.viewState.value.shop, editProduct.shop)
        assertEquals(viewModel.viewState.value.description, editProduct.description)
        assertEquals(viewModel.viewState.value.productName, editProduct.name)
        assertFalse(viewModel.viewState.value.isNewProduct)
    }

    @Test
    fun `with editProduct, on setDescription sets description`() {
        viewModel.viewState.value.setDescription(DESCRIPTION)
        assertEquals(viewModel.viewState.value.description, DESCRIPTION)
    }

    @Test
    fun `with editProduct, on setName sets name`() {
        viewModel.viewState.value.setName(NAME)
        assertEquals(viewModel.viewState.value.productName, NAME)
    }

    @Test
    fun `with editProduct on setShop sets shop`() {
        viewModel.viewState.value.setShop(SHOP)
        assertEquals(viewModel.viewState.value.shop, SHOP)
    }

    @Test
    fun `with editProduct, on onQuit calls dataCleaner`() {
        coEvery { backupImagesRepository.getAllByProductId(any()) } returns editProductImages
        coEvery { productsRepository.updateImagesInProduct(any(), any()) } just Runs
        coEvery { productImageManager.moveFromBackupToOriginalFolder(any()) } just Runs
        coEvery { dataCleaner.deleteTempFolder(any()) } just Runs
        coEvery { dataCleaner.deleteBackupFolder(any()) } just Runs
        coEvery { backupImagesRepository.deleteImagesByProductId(any()) } just Runs

        assertEquals(viewModel.viewState.value.quitStatus, QuitStatus.Initial)

        viewModel.viewState.value.onQuit()

        val productFolderName = viewModel.viewState.value.editProduct!!.productFolderName

        coVerify {
            productsRepository.updateImagesInProduct(
                id = getEditProductId(),
                images = editProductImages
            )
        }
        coVerify {
            productImageManager.moveFromBackupToOriginalFolder(
                productFolderName
            )
        }
        coVerify {
            dataCleaner.deleteTempFolder(
                productFolderName = productFolderName
            )
        }
        coVerify {
            dataCleaner.deleteBackupFolder(
                productFolderName = productFolderName
            )
        }
        coVerify {
            backupImagesRepository.deleteImagesByProductId(getEditProductId())
        }

        assertEquals(viewModel.viewState.value.quitStatus, QuitStatus.Finish)
    }

    @Test
    fun `with editProduct, on onTypeClicked, type is changed`() {
        viewModel.viewState.value.onTypeClicked(HateRateType.HATE)

        assertEquals(viewModel.viewState.value.type, HateRateType.HATE)
    }

    @Test
    fun `with editProduct, on onShowAlertDialog true, showAlertDialog value changes to true`() {
        viewModel.viewState.value.onShowAlertDialog(true)

        assertTrue(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun `with editProduct, on onForceQuit, forceQUit should be true`() {
        viewModel.viewState.value.onForceQuit()

        assertTrue(viewModel.viewState.value.forceQuit)
    }

    @Test
    fun `with editProduct, onRemoveImageClicked with success delete, should delete image`() {
        every { fileUtils.deleteFile(uri = any()) } returns true
        coEvery { productsRepository.deleteProductImage(any(), any()) } just Runs

        val firstImage = createImageData()
        val imageToDelete = createImageData()

        prepareImage(firstImage)
        prepareImage(imageToDelete)

        viewModel.viewState.value.onRemoveImageClicked(imageToDelete)

        verify { fileUtils.deleteFile(imageToDelete.uri) }
        coVerify {
            productsRepository.deleteProductImage(
                productId = getEditProductId(),
                imageName = imageToDelete.name
            )
        }

        assertTrue(viewModel.viewState.value.images.size == 1)
        assertEquals(viewModel.viewState.value.images.first(), firstImage)
    }

    @Test
    fun `with editProduct, onAddImageFromGalleryClicked should add image`() {
        val imageData = createImageData()
        every { fileUtils.getFileUriFromGalleryUri(any(), any(), any()) } returns imageData

        assertTrue(viewModel.viewState.value.images.isEmpty())

        viewModel.viewState.value.onAddImageFromGalleryClicked(imageData.uri)

        verify {
            fileUtils.getFileUriFromGalleryUri(
                uri = imageData.uri,
                folderName = getProductFolderName(),
                isEdit = true,
            )
        }

        assertTrue(viewModel.viewState.value.images.size == 1)
    }

    @Test
    fun `with editProduct, onAddCameraPictureClicked should add image`() {
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
                isEdit = true,
            )
        }

        assertTrue(viewModel.viewState.value.images.size == 1)
    }

    @Test
    fun `with editProduct, getCameraImageFileUri should return correct CameraTakePictureData`() {
        val cameraTakePictureData = createCameraTakePictureData()

        every { fileUtils.getFileUriForTakePicture(any(), any()) } returns cameraTakePictureData

        val cameraData = viewModel.viewState.value.getCameraImageFileUri.invoke()

        verify {
            fileUtils.getFileUriForTakePicture(
                folderName = getProductFolderName(),
                isEdit = true,
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
        requireNotNull(viewModel.viewState.value.editProduct).productFolderName

    private fun getEditProductId(): Long =
        savedStateHandle.get<String?>(RootNavDestinations.ProductManager.KEY_EDIT_PRODUCT_ID)!!.toLong()
}