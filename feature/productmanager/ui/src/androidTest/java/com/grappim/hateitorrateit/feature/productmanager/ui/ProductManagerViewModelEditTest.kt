package com.grappim.hateitorrateit.feature.productmanager.ui

import com.grappim.hateitorrateit.data.analyticsapi.ProductManagerAnalytics
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.BackupImagesRepository
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.feature.productmanager.ui.navigation.ProductManagerNavDestination
import com.grappim.hateitorrateit.testing.core.MainDispatcherRule
import com.grappim.hateitorrateit.testing.core.SavedStateHandleRule
import com.grappim.hateitorrateit.testing.core.createCameraTakePictureData
import com.grappim.hateitorrateit.testing.core.createImageData
import com.grappim.hateitorrateit.testing.domain.DESCRIPTION
import com.grappim.hateitorrateit.testing.domain.NAME
import com.grappim.hateitorrateit.testing.domain.PRODUCT_FOLDER_NAME
import com.grappim.hateitorrateit.testing.domain.PRODUCT_ID
import com.grappim.hateitorrateit.testing.domain.SHOP
import com.grappim.hateitorrateit.testing.domain.TYPE
import com.grappim.hateitorrateit.testing.domain.createEditProduct
import com.grappim.hateitorrateit.testing.domain.editProduct
import com.grappim.hateitorrateit.testing.domain.editProductImages
import com.grappim.hateitorrateit.utils.filesapi.deletion.FileDeletionUtils
import com.grappim.hateitorrateit.utils.filesapi.images.ImagePersistenceManager
import com.grappim.hateitorrateit.utils.filesapi.mappers.ImageDataMapper
import com.grappim.hateitorrateit.utils.filesapi.models.ProductImageUIData
import com.grappim.hateitorrateit.utils.filesapi.productmanager.ProductImageManager
import com.grappim.hateitorrateit.utils.filesapi.urimanager.FileUriManager
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProductManagerViewModelEditTest {
    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val route = ProductManagerNavDestination(PRODUCT_ID)

    @get:Rule
    val savedStateHandleRule = SavedStateHandleRule(route)

    private val productsRepository: ProductsRepository = mockk()
    private val dataCleaner: DataCleaner = mockk()
    private val localDataStorage: LocalDataStorage = mockk()
    private val backupImagesRepository: BackupImagesRepository = mockk()
    private val productImageManager: ProductImageManager = mockk()
    private val imageDataMapper: ImageDataMapper = mockk()
    private val productManagerAnalytics: ProductManagerAnalytics = mockk()
    private val imagePersistenceManager: ImagePersistenceManager = mockk()
    private val fileDeletionUtils: FileDeletionUtils = mockk()
    private val fileUriManager: FileUriManager = mockk()
    private lateinit var viewModel: ProductManagerViewModel

    @Before
    fun setup() {
        every { localDataStorage.typeFlow } returns flowOf(TYPE)

        coEvery { productsRepository.getProductById(any()) } returns editProduct
        coEvery { productImageManager.copyToBackupFolder(any()) } just Runs
        coEvery { backupImagesRepository.insertImages(any(), any()) } just Runs
        coEvery { imageDataMapper.toImageDataList(any()) } returns persistentListOf()

        viewModel = ProductManagerViewModel(
            productsRepository = productsRepository,
            dataCleaner = dataCleaner,
            localDataStorage = localDataStorage,
            backupImagesRepository = backupImagesRepository,
            productImageManager = productImageManager,
            imageDataMapper = imageDataMapper,
            productManagerAnalytics = productManagerAnalytics,
            savedStateHandle = savedStateHandleRule.savedStateHandleMock,
            imagePersistenceManager = imagePersistenceManager,
            fileDeletionUtils = fileDeletionUtils,
            fileUriManager = fileUriManager
        )
    }

    @Test
    fun on_trackOnScreenStart_should_call_correct_analytics_event() {
        every { productManagerAnalytics.trackProductManagerProductToEditStart() } just Runs

        viewModel.viewState.value.trackOnScreenStart()

        verify { productManagerAnalytics.trackProductManagerProductToEditStart() }
    }

    @Test
    fun on_init_with_editProduct_edit_product_will_be_prepared() {
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
    fun with_editProduct_on_setDescription_sets_description() {
        viewModel.viewState.value.setDescription(DESCRIPTION)
        assertEquals(viewModel.viewState.value.description, DESCRIPTION)
    }

    @Test
    fun with_editProduct_on_setName_sets_name() {
        viewModel.viewState.value.setName(NAME)
        assertEquals(viewModel.viewState.value.productName, NAME)
    }

    @Test
    fun with_editProduct_on_setShop_sets_shop() {
        viewModel.viewState.value.setShop(SHOP)
        assertEquals(viewModel.viewState.value.shop, SHOP)
    }

    @Test
    fun with_editProduct_on_onQuit_calls_dataCleaner() {
        coEvery { backupImagesRepository.getAllByProductId(any()) } returns editProductImages
        coEvery { productsRepository.updateImagesInProduct(any(), any()) } just Runs
        coEvery { productImageManager.moveFromBackupToOriginalFolder(any()) } just Runs
        coEvery { dataCleaner.deleteTempFolder(any()) } just Runs
        coEvery { dataCleaner.deleteBackupFolder(any()) } just Runs
        coEvery { backupImagesRepository.deleteImagesByProductId(any()) } just Runs

        viewModel.viewState.value.onGoBack()

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
    }

    @Test
    fun with_editProduct_on_onTypeClicked_type_is_changed() {
        viewModel.viewState.value.onTypeClicked(HateRateType.HATE)

        assertEquals(viewModel.viewState.value.type, HateRateType.HATE)
    }

    @Test
    fun with_editProduct_on_onTypeClicked_with_the_same_type_type_is_not_changed() {
        viewModel.viewState.value.onTypeClicked(TYPE)

        assertEquals(viewModel.viewState.value.type, TYPE)
    }

    @Test
    fun with_editProduct_on_onShowAlertDialog_true_showAlertDialog_value_changes_to_true() {
        viewModel.viewState.value.onShowAlertDialog(true)

        assertTrue(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun with_editProduct_onRemoveImageClicked_with_success_delete_should_delete_image() {
        coEvery { fileDeletionUtils.deleteFile(uri = any()) } returns true
        coEvery { productsRepository.deleteProductImage(any(), any()) } just Runs
        every { productManagerAnalytics.trackGalleryButtonClicked() } just Runs
        every { productManagerAnalytics.trackDeleteImageClicked() } just Runs

        val firstImage = createImageData()
        val imageToDelete = createImageData()

        prepareImage(firstImage)
        prepareImage(imageToDelete)

        viewModel.viewState.value.onDeleteImageClicked(imageToDelete)

        coVerify { fileDeletionUtils.deleteFile(imageToDelete.uri) }
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
    fun with_editProduct_onAddImageFromGalleryClicked_should_add_image() {
        val imageData = createImageData()
        coEvery { fileUriManager.getFileUriFromGalleryUri(any(), any(), any()) } returns imageData
        every { productManagerAnalytics.trackGalleryButtonClicked() } just Runs

        assertTrue(viewModel.viewState.value.images.isEmpty())

        viewModel.viewState.value.onAddImageFromGalleryClicked(imageData.uri)

        coVerify {
            fileUriManager.getFileUriFromGalleryUri(
                uri = imageData.uri,
                folderName = getProductFolderName(),
                isEdit = true
            )
        }

        assertTrue(viewModel.viewState.value.images.size == 1)
    }

    @Test
    fun with_editProduct_productManagerAnalytics_should_call_correct_event() {
        val imageData = createImageData()
        coEvery { fileUriManager.getFileUriFromGalleryUri(any(), any(), any()) } returns imageData
        every { productManagerAnalytics.trackGalleryButtonClicked() } just Runs

        viewModel.viewState.value.onAddImageFromGalleryClicked(imageData.uri)

        verify { productManagerAnalytics.trackGalleryButtonClicked() }
    }

    @Test
    fun with_editProduct_onAddCameraPictureClicked_should_add_image() {
        val cameraTakePictureData = createCameraTakePictureData()
        val imageData = createImageData(
            newUri = cameraTakePictureData.uri
        )

        every { fileUriManager.getFileDataFromCameraPicture(any(), any()) } returns imageData
        every { productManagerAnalytics.trackCameraButtonClicked() } just Runs

        assertTrue(viewModel.viewState.value.images.isEmpty())

        viewModel.viewState.value.onAddCameraPictureClicked(cameraTakePictureData)

        verify {
            fileUriManager.getFileDataFromCameraPicture(
                cameraTakePictureData = cameraTakePictureData,
                isEdit = true
            )
        }

        assertTrue(viewModel.viewState.value.images.size == 1)
    }

    @Test
    fun with_editProduct_productManagerAnalytics_calls_correct_event() {
        val cameraTakePictureData = createCameraTakePictureData()
        val imageData = createImageData(
            newUri = cameraTakePictureData.uri
        )

        every { fileUriManager.getFileDataFromCameraPicture(any(), any()) } returns imageData
        every { productManagerAnalytics.trackCameraButtonClicked() } just Runs

        viewModel.viewState.value.onAddCameraPictureClicked(cameraTakePictureData)

        verify { productManagerAnalytics.trackCameraButtonClicked() }
    }

    @Test
    fun with_editProduct_getCameraImageFileUri_should_return_correct_CameraTakePictureData() {
        val cameraTakePictureData = createCameraTakePictureData()

        every {
            fileUriManager.getFileUriForTakePicture(any(), any())
        } returns cameraTakePictureData

        val cameraData = viewModel.viewState.value.getCameraImageFileUri.invoke()

        verify {
            fileUriManager.getFileUriForTakePicture(
                folderName = getProductFolderName(),
                isEdit = true
            )
        }

        assertEquals(cameraData, cameraTakePictureData)
    }

    @Test
    fun with_editProduct_on_onProductDone_product_is_saved() {
        val editProduct = createEditProduct(editProductImages)
        coEvery { productsRepository.getProductById(any()) } returns editProduct

        coEvery { imageDataMapper.toProductImageDataList(any()) } returns editProductImages
        coEvery {
            fileUriManager.getFileUriFromGalleryUri(
                any(),
                any(),
                any()
            )
        } returns productImageUIData
        every { productManagerAnalytics.trackSaveButtonClicked() } just Runs
        every { productManagerAnalytics.trackGalleryButtonClicked() } just Runs
        coEvery {
            imagePersistenceManager.prepareEditedImagesToPersist(any())
        } returns editProductImages

        coEvery { productImageManager.moveFromTempToOriginalFolder(any()) } just Runs
        coEvery { dataCleaner.deleteTempFolder(any()) } just Runs
        coEvery { dataCleaner.deleteBackupFolder(any()) } just Runs
        coEvery { backupImagesRepository.deleteImagesByProductId(any()) } just Runs
        coEvery { productsRepository.updateProductWithImages(any(), any()) } just Runs

        viewModel.viewState.value.onAddImageFromGalleryClicked(uri)

        viewModel.viewState.value.onProductDone()

        coVerify {
            imagePersistenceManager.prepareEditedImagesToPersist(listOf(productImageUIData))
        }
        coVerify { productImageManager.moveFromTempToOriginalFolder(PRODUCT_FOLDER_NAME) }
        coVerify { dataCleaner.deleteTempFolder(PRODUCT_FOLDER_NAME) }
        coVerify { dataCleaner.deleteBackupFolder(PRODUCT_FOLDER_NAME) }
        coVerify { backupImagesRepository.deleteImagesByProductId(PRODUCT_ID) }
        coVerify {
            productsRepository.updateProductWithImages(
                product = editProduct,
                images = editProductImages
            )
        }

        assertTrue(viewModel.viewState.value.productSaved)
    }

    @Test
    fun with_editProduct_on_onProductDone_productManagerAnalytics_should_call_trackSaveButtonClicked() {
        coEvery { productsRepository.addProduct(any()) } just Runs
        coEvery { imageDataMapper.toProductImageDataList(any()) } returns editProductImages
        coEvery {
            fileUriManager.getFileUriFromGalleryUri(
                any(),
                any(),
                any()
            )
        } returns productImageUIData
        every { productManagerAnalytics.trackSaveButtonClicked() } just Runs
        every { productManagerAnalytics.trackGalleryButtonClicked() } just Runs
        coEvery {
            imagePersistenceManager.prepareEditedImagesToPersist(any())
        } returns editProductImages

        coEvery { productImageManager.moveFromTempToOriginalFolder(any()) } just Runs
        coEvery { dataCleaner.deleteTempFolder(any()) } just Runs
        coEvery { dataCleaner.deleteBackupFolder(any()) } just Runs
        coEvery { backupImagesRepository.deleteImagesByProductId(any()) } just Runs
        coEvery { productsRepository.updateProductWithImages(any(), any()) } just Runs

        viewModel.viewState.value.onAddImageFromGalleryClicked(uri)

        viewModel.viewState.value.onProductDone()

        verify { productManagerAnalytics.trackSaveButtonClicked() }
    }

    private fun prepareImage(productImageUIData: ProductImageUIData) {
        coEvery {
            fileUriManager.getFileUriFromGalleryUri(any(), any(), any())
        } returns productImageUIData
        viewModel.viewState.value.onAddImageFromGalleryClicked(productImageUIData.uri)
    }

    private fun getProductFolderName(): String = requireNotNull(viewModel.viewState.value.editProduct).productFolderName

    private fun getEditProductId(): Long = requireNotNull(route.productId)
}
