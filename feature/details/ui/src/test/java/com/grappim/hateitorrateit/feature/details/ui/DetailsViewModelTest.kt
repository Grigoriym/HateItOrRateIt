package com.grappim.hateitorrateit.feature.details.ui

import android.content.Intent
import app.cash.turbine.test
import com.grappim.hateitorrateit.data.analyticsapi.DetailsAnalytics
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.data.repoapi.models.Product
import com.grappim.hateitorrateit.feature.details.ui.mappers.UiModelsMapper
import com.grappim.hateitorrateit.feature.details.ui.model.ProductDetailsUi
import com.grappim.hateitorrateit.feature.details.ui.navigation.DetailsNavDestination
import com.grappim.hateitorrateit.testing.core.MainDispatcherRule
import com.grappim.hateitorrateit.testing.core.SavedStateHandleRule
import com.grappim.hateitorrateit.testing.core.testException
import com.grappim.hateitorrateit.testing.domain.NAME
import com.grappim.hateitorrateit.testing.domain.PRODUCT_FOLDER_NAME
import com.grappim.hateitorrateit.testing.domain.PRODUCT_ID
import com.grappim.hateitorrateit.testing.domain.createRandomProductImage
import com.grappim.hateitorrateit.testing.domain.createRandomProductImageList
import com.grappim.hateitorrateit.utils.androidapi.GalleryInteractions
import com.grappim.hateitorrateit.utils.androidapi.IntentGenerator
import com.grappim.hateitorrateit.utils.ui.NativeText
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DetailsViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val route = DetailsNavDestination(PRODUCT_ID)

    @get:Rule
    val savedStateHandleRule = SavedStateHandleRule(route)

    private val productsRepository: ProductsRepository = mockk()
    private val uiModelsMapper: UiModelsMapper = mockk()
    private val dataCleaner: DataCleaner = mockk()
    private val detailsAnalytics: DetailsAnalytics = mockk()
    private val galleryInteractions: GalleryInteractions = mockk()
    private val intentGenerator: IntentGenerator = mockk()

    private lateinit var sut: DetailsViewModel

    private val product = Product(
        id = PRODUCT_ID,
        name = NAME,
        description = "description",
        shop = "shop",
        type = HateRateType.HATE,
        productFolderName = PRODUCT_FOLDER_NAME,
        createdDate = OffsetDateTime.now(),
        images = createRandomProductImageList()
    )

    private val productUi = ProductDetailsUi(
        id = PRODUCT_ID.toString(),
        name = NAME,
        createdDate = "reque",
        images = createRandomProductImageList(),
        productFolderName = PRODUCT_FOLDER_NAME,
        description = "felis",
        shop = "fabellas",
        type = HateRateType.HATE
    )

    @Before
    fun setup() {
        coEvery {
            productsRepository.getProductById(any())
        } returns product
        coEvery { uiModelsMapper.toProductDetailsUi(any()) } returns productUi

        every { intentGenerator.generateAppSettingsIntent() } returns Intent()

        sut = DetailsViewModel(
            productsRepository = productsRepository,
            uiModelsMapper = uiModelsMapper,
            dataCleaner = dataCleaner,
            detailsAnalytics = detailsAnalytics,
            savedStateHandle = savedStateHandleRule.savedStateHandleMock,
            galleryInteractions = galleryInteractions,
            intentGenerator = intentGenerator
        )
    }

    @Test
    fun `on trackScreenStart should call trackScreenStart event`() {
        every { detailsAnalytics.trackDetailsScreenStart() } just Runs

        sut.viewState.value.trackScreenStart()

        verify { detailsAnalytics.trackDetailsScreenStart() }
    }

    @Test
    fun `on trackEditButtonClicked should call trackDetailsEditButtonClicked event`() {
        every { detailsAnalytics.trackDetailsEditButtonClicked() } just Runs

        sut.viewState.value.trackEditButtonClicked()

        verify { detailsAnalytics.trackDetailsEditButtonClicked() }
    }

    @Test
    fun `on onDeleteProductConfirm, clearProductData should be called and productDeleted is true`() {
        coEvery { dataCleaner.deleteProductData(any(), any()) } just Runs
        every { detailsAnalytics.trackDetailsDeleteProductConfirmed() } just Runs

        sut.viewState.value.onDeleteProductConfirm()

        assertTrue(sut.viewState.value.isLoading)
        assertEquals(sut.viewState.value.productId, PRODUCT_ID.toString())
        coVerify {
            dataCleaner.deleteProductData(
                productId = PRODUCT_ID,
                productFolderName = PRODUCT_FOLDER_NAME
            )
        }
        assertTrue(sut.viewState.value.productDeleted)

        verify { detailsAnalytics.trackDetailsDeleteProductConfirmed() }
    }

    @Test
    fun `onShowAlertDialog, showAlertDialog should be true`() {
        assertFalse(sut.viewState.value.showAlertDialog)
        sut.viewState.value.onShowAlertDialog(true)
        assertTrue(sut.viewState.value.showAlertDialog)
    }

    @Test
    fun `onDeleteProduct, showAlertDialog should be true`() {
        every { detailsAnalytics.trackDetailsDeleteProductButtonClicked() } just Runs
        assertFalse(sut.viewState.value.showAlertDialog)

        sut.viewState.value.onDeleteProduct()

        assertTrue(sut.viewState.value.showAlertDialog)

        verify { detailsAnalytics.trackDetailsDeleteProductButtonClicked() }
    }

    @Test
    fun `on updateProduct should getProduct`() {
        sut.viewState.value.updateProduct()

        coVerify { productsRepository.getProductById(PRODUCT_ID) }
        coVerify { uiModelsMapper.toProductDetailsUi(product) }

        val state = sut.viewState.value

        assertEquals(state.productId, PRODUCT_ID.toString())
        assertEquals(state.name, NAME)
        assertFalse(state.isLoading)
        assertEquals(state.productFolderName, PRODUCT_FOLDER_NAME)
    }

    @Test
    fun `on resetSaveFileToGalleryState should make state Initial`() = runTest {
        coEvery {
            galleryInteractions.saveImageInGallery(
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.failure(testException)

        sut.viewEvents.test {
            val image = createRandomProductImage()
            sut.viewState.value.saveFileToGallery(image)
            assertEquals(DetailsEvents.SaveImageFailure, awaitItem())
        }
    }

    @Test
    fun `onShowPermissionsAlertDialog should correct pass the data`() {
        val text = "messageT"
        assertFalse(sut.viewState.value.showProvidePermissionsAlertDialog)
        sut.viewState.value.onShowPermissionsAlertDialog(true, text)
        assertTrue(sut.viewState.value.showProvidePermissionsAlertDialog)
        assertEquals(text, sut.viewState.value.permissionsAlertDialogText)
    }

    @Test
    fun `on clearShareImageIntent should make the intent null`() {
        val productImage = createRandomProductImage()
        val expected = Intent("someAction")

        every {
            intentGenerator.generateIntentToShareImage(
                productImage.uriString,
                productImage.mimeType
            )
        } returns expected

        assertNull(sut.viewState.value.shareImageIntent)

        sut.viewState.value.onShareImageClick(productImage)

        assertEquals(expected, sut.viewState.value.shareImageIntent)

        sut.viewState.value.clearShareImageIntent()

        assertNull(sut.viewState.value.shareImageIntent)
    }

    @Test
    fun `on onShareImageClicked should generate the intent`() {
        val productImage = createRandomProductImage()
        val expected = Intent("someAction")
        every {
            intentGenerator.generateIntentToShareImage(
                productImage.uriString,
                productImage.mimeType
            )
        } returns expected

        sut.viewState.value.onShareImageClick(productImage)

        assertEquals(expected, sut.viewState.value.shareImageIntent)
    }

    @Test
    fun `on saveFileToGallery should emit correct state`() = runTest {
        val image = createRandomProductImage()
        val expected = DetailsEvents.SaveImageSuccess
        coEvery {
            galleryInteractions.saveImageInGallery(
                any(),
                any(),
                any(),
                any()
            )
        } returns Result.success(Unit)

        sut.viewEvents.test {
            sut.viewState.value.saveFileToGallery(image)
            assertEquals(expected, awaitItem())
        }
    }

    @Test
    fun `on setCurrentDisplayedImageIndex should set correct image`() {
        val expected = productUi.images.last()
        sut.viewState.value.setCurrentDisplayedImageIndex(product.images.lastIndex)

        assertEquals(expected, sut.viewState.value.currentImage)
    }

    @Test
    fun `on setSnackbarMessage should set correct message`() = runTest {
        val expected = NativeText.Simple("sdfsdfsdf")
        sut.snackBarMessage.test {
            sut.viewState.value.setSnackbarMessage(expected)
            assertEquals(expected, awaitItem())
        }
    }
}
