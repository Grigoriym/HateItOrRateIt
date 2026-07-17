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
import com.grappim.hateitorrateit.testing.core.createProduct
import com.grappim.hateitorrateit.testing.domain.DESCRIPTION
import com.grappim.hateitorrateit.testing.domain.NAME
import com.grappim.hateitorrateit.testing.domain.SHOP
import com.grappim.hateitorrateit.testing.domain.TYPE
import com.grappim.hateitorrateit.testing.domain.draftProduct
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
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductManagerViewModelDraftTest {
    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val route = ProductManagerNavDestination(null)

    @get:Rule
    val savedStateHandleRule = SavedStateHandleRule(route)

    private val productsRepository: ProductsRepository = mockk()
    private val dataCleaner: DataCleaner = mockk()
    private val localDataStorage: LocalDataStorage = mockk()
    private val backupImagesRepository: BackupImagesRepository = mockk()
    private val productImageManager: ProductImageManager = mockk()
    private val imageDataMapper: ImageDataMapper = mockk()
    private val productManagerAnalytics: ProductManagerAnalytics = mockk()
    private val fileUriManager: FileUriManager = mockk()
    private val fileDeletionUtils: FileDeletionUtils = mockk()
    private val imagePersistenceManager: ImagePersistenceManager = mockk()
    private lateinit var viewModel: ProductManagerViewModel

    @Before
    fun setup() {
        coEvery { productsRepository.addDraftProduct() } returns draftProduct
        every { localDataStorage.typeFlow } returns flowOf(TYPE)

        viewModel = ProductManagerViewModel(
            productsRepository = productsRepository,
            dataCleaner = dataCleaner,
            localDataStorage = localDataStorage,
            backupImagesRepository = backupImagesRepository,
            productImageManager = productImageManager,
            imageDataMapper = imageDataMapper,
            productManagerAnalytics = productManagerAnalytics,
            savedStateHandle = savedStateHandleRule.savedStateHandleMock,
            fileUriManager = fileUriManager,
            fileDeletionUtils = fileDeletionUtils,
            imagePersistenceManager = imagePersistenceManager
        )
    }

    @Test
    fun on_trackOnScreenStart_should_call_correct_analytics_event() {
        every { productManagerAnalytics.trackProductManagerNewProductStart() } just Runs

        viewModel.viewState.value.trackOnScreenStart()

        verify { productManagerAnalytics.trackProductManagerNewProductStart() }
    }

    @Test
    fun on_init_without_draftProduct_draftProduct_will_be_created() {
        coVerify { productsRepository.addDraftProduct() }

        assertEquals(viewModel.viewState.value.draftProduct, draftProduct)
        assertEquals(viewModel.viewState.value.type, draftProduct.type)
        assertTrue(viewModel.viewState.value.isNewProduct)
    }

    @Test
    fun with_draftProduct_on_setDescription_sets_description() {
        viewModel.viewState.value.setDescription(DESCRIPTION)
        assertEquals(viewModel.viewState.value.description, DESCRIPTION)
    }

    @Test
    fun with_draftProduct_on_setName_sets_name() {
        viewModel.viewState.value.setName(NAME)
        assertEquals(viewModel.viewState.value.productName, NAME)
    }

    @Test
    fun with_draftProduct_on_setShop_sets_shop() {
        viewModel.viewState.value.setShop(SHOP)
        assertEquals(viewModel.viewState.value.shop, SHOP)
    }

    @Test
    fun with_draftProduct_on_onProductDone_new_product_is_created() {
        coEvery { productsRepository.addProduct(any()) } just Runs
        coEvery { imageDataMapper.toProductImageDataList(any()) } returns editProductImages
        coEvery {
            fileUriManager.getFileUriFromGalleryUri(
                any(),
                any(),
                any()
            )
        } returns productImageUIData
        every { productManagerAnalytics.trackCreateButtonClicked() } just Runs
        every { productManagerAnalytics.trackGalleryButtonClicked() } just Runs

        viewModel.viewState.value.setName(NAME)
        viewModel.viewState.value.setDescription(DESCRIPTION)
        viewModel.viewState.value.setShop(SHOP)
        viewModel.viewState.value.onAddImageFromGalleryClicked(uri)

        viewModel.viewState.value.onProductDone()

        coVerify { productsRepository.addProduct(createProduct) }

        assertTrue(viewModel.viewState.value.productSaved)
    }

    @Test
    fun with_draftProduct_on_onProductDone_productManagerAnalytics_should_call_trackCreateButtonClicked() {
        coEvery { productsRepository.addProduct(any()) } just Runs
        coEvery { imageDataMapper.toProductImageDataList(any()) } returns editProductImages
        coEvery {
            fileUriManager.getFileUriFromGalleryUri(
                any(),
                any(),
                any()
            )
        } returns productImageUIData
        every { productManagerAnalytics.trackCreateButtonClicked() } just Runs
        every { productManagerAnalytics.trackGalleryButtonClicked() } just Runs

        viewModel.viewState.value.setName(NAME)
        viewModel.viewState.value.setDescription(DESCRIPTION)
        viewModel.viewState.value.setShop(SHOP)
        viewModel.viewState.value.onAddImageFromGalleryClicked(uri)

        viewModel.viewState.value.onProductDone()

        verify { productManagerAnalytics.trackCreateButtonClicked() }
    }

    @Test
    fun with_draftProduct_on_onTypeClicked_type_is_changed() {
        viewModel.viewState.value.onTypeClicked(HateRateType.HATE)

        assertEquals(viewModel.viewState.value.type, HateRateType.HATE)
    }

    @Test
    fun with_draftProduct_on_onTypeClicked_with_the_same_type_type_is_not_changed() {
        viewModel.viewState.value.onTypeClicked(TYPE)

        assertEquals(viewModel.viewState.value.type, TYPE)
    }

    @Test
    fun with_draftProduct_on_onShowAlertDialog_true_showAlertDialog_value_changes_to_true() {
        viewModel.viewState.value.onShowAlertDialog(true)

        assertTrue(viewModel.viewState.value.showAlertDialog)
    }

    @Test
    fun with_draftProduct_onRemoveImageClicked_with_success_delete_should_delete_image() {
        coEvery { fileDeletionUtils.deleteFile(uri = any()) } returns true
        every { productManagerAnalytics.trackGalleryButtonClicked() } just Runs
        every { productManagerAnalytics.trackDeleteImageClicked() } just Runs

        val firstImage = createImageData()
        val imageToDelete = createImageData()

        prepareImage(firstImage)
        prepareImage(imageToDelete)

        viewModel.viewState.value.onDeleteImageClicked(imageToDelete)

        coVerify { fileDeletionUtils.deleteFile(imageToDelete.uri) }

        assertTrue(viewModel.viewState.value.images.size == 1)
        assertEquals(viewModel.viewState.value.images.first(), firstImage)
    }

    @Test
    fun with_draftProduct_onAddImageFromGalleryClicked_should_add_image() {
        val imageData = createImageData()
        coEvery { fileUriManager.getFileUriFromGalleryUri(any(), any(), any()) } returns imageData
        every { productManagerAnalytics.trackGalleryButtonClicked() } just Runs

        assertTrue(viewModel.viewState.value.images.isEmpty())

        viewModel.viewState.value.onAddImageFromGalleryClicked(imageData.uri)

        coVerify {
            fileUriManager.getFileUriFromGalleryUri(
                uri = imageData.uri,
                folderName = getProductFolderName(),
                isEdit = false
            )
        }

        assertTrue(viewModel.viewState.value.images.size == 1)

        verify { productManagerAnalytics.trackGalleryButtonClicked() }
    }

    @Test
    fun with_draftProduct_onAddCameraPictureClicked_should_add_image() {
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
                isEdit = false
            )
        }

        assertTrue(viewModel.viewState.value.images.size == 1)

        verify { productManagerAnalytics.trackCameraButtonClicked() }
    }

    @Test
    fun with_draftProduct_getCameraImageFileUri_should_return_correct_CameraTakePictureData() {
        val cameraTakePictureData = createCameraTakePictureData()

        every {
            fileUriManager.getFileUriForTakePicture(
                any(),
                any()
            )
        } returns cameraTakePictureData

        val cameraData = viewModel.viewState.value.getCameraImageFileUri.invoke()

        verify {
            fileUriManager.getFileUriForTakePicture(
                folderName = getProductFolderName(),
                isEdit = false
            )
        }

        assertEquals(cameraData, cameraTakePictureData)
    }

    private fun prepareImage(productImageUIData: ProductImageUIData) {
        coEvery {
            fileUriManager.getFileUriFromGalleryUri(any(), any(), any())
        } returns productImageUIData
        viewModel.viewState.value.onAddImageFromGalleryClicked(productImageUIData.uri)
    }

    private fun getProductFolderName(): String = requireNotNull(viewModel.viewState.value.draftProduct).productFolderName
}
