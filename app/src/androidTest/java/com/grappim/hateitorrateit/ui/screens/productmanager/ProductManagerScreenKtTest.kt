package com.grappim.hateitorrateit.ui.screens.productmanager

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.ui.R
import com.grappim.hateitorrateit.ui.widgets.PLATO_HATE_RATE_CONTENT_TAG
import com.grappim.hateitorrateit.utils.filesapi.models.CameraTakePictureData
import com.grappim.hateitorrateit.utils.ui.LaunchedEffectResult
import com.grappim.hateitorrateit.utils.ui.NativeText
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val PRODUCT_NAME = "product_name_test"
private const val DESCRIPTION = "description_test"
private const val SHOP = "shop_test"

class ProductManagerScreenKtTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var editNameText: String
    private lateinit var editDescriptionName: String
    private lateinit var editShopName: String
    private lateinit var addPictureTitle: String
    private lateinit var cameraButton: String
    private lateinit var galleryButton: String
    private lateinit var draftCreateButton: String
    private lateinit var editCreateButton: String

    @Before
    fun setup() {
        composeTestRule.activity.apply {
            editNameText = getString(R.string.name_obligatory)
            editDescriptionName = getString(R.string.description)
            editShopName = getString(R.string.shop)
            addPictureTitle = getString(R.string.add_picture_from)
            cameraButton = getString(R.string.camera)
            galleryButton = getString(R.string.gallery)
            draftCreateButton = getString(R.string.create)
            editCreateButton = getString(R.string.save)
        }
    }

    @Test
    fun on_draft_verify_initial_state_is_correctly_shown() {
        composeTestRule.run {
            setContent {
                ProductManagerScreen(
                    state = getState().copy(
                        bottomBarButtonText = NativeText.Resource(R.string.create),
                        alertDialogText = NativeText.Resource(R.string.if_quit_lose_data)
                    ),
                    goBack = {},
                    onProductCreated = {},
                    snackBarMessage = LaunchedEffectResult(NativeText.Empty)
                )
            }

            onNodeWithText(editNameText).assertIsDisplayed()
            onNodeWithText(editDescriptionName).assertIsDisplayed()
            onNodeWithText(editShopName).assertIsDisplayed()

            onNodeWithTag(PLATO_HATE_RATE_CONTENT_TAG).assertIsDisplayed()

            onNodeWithText(addPictureTitle).assertIsDisplayed()

            onNodeWithText(cameraButton).assertIsDisplayed()
            onNodeWithText(galleryButton).assertIsDisplayed()

            onNodeWithText(draftCreateButton).assertIsDisplayed()
        }
    }

    @Test
    fun on_edit_verify_initial_state_is_correctly_shown() {
        composeTestRule.run {
            setContent {
                ProductManagerScreen(
                    state = getState().copy(
                        bottomBarButtonText = NativeText.Resource(R.string.save),
                        alertDialogText = NativeText.Resource(R.string.if_quit_ensure_saved)
                    ),
                    goBack = {},
                    onProductCreated = {},
                    snackBarMessage = LaunchedEffectResult(NativeText.Empty)
                )
            }

            onNodeWithText(editNameText).assertIsDisplayed()
            onNodeWithText(editDescriptionName).assertIsDisplayed()
            onNodeWithText(editShopName).assertIsDisplayed()

            onNodeWithText(PRODUCT_NAME).assertIsDisplayed()
            onNodeWithText(DESCRIPTION).assertIsDisplayed()
            onNodeWithText(SHOP).assertIsDisplayed()

            onNodeWithTag(PLATO_HATE_RATE_CONTENT_TAG).assertIsDisplayed()

            onNodeWithText(addPictureTitle).assertIsDisplayed()

            onNodeWithText(cameraButton).assertIsDisplayed()
            onNodeWithText(galleryButton).assertIsDisplayed()

            onNodeWithText(editCreateButton).assertIsDisplayed()
        }
    }

    @Test
    fun verify_edit_content_is_correctly_filled() {
        composeTestRule.run {
            setContent {
                ProductManagerScreen(
                    state = getState().copy(
                        productName = PRODUCT_NAME,
                        description = DESCRIPTION,
                        shop = SHOP
                    ),
                    goBack = {},
                    onProductCreated = {},
                    snackBarMessage = LaunchedEffectResult(NativeText.Empty)
                )
            }

            onNodeWithText(editNameText).assertIsDisplayed()
            onNodeWithText(editDescriptionName).assertIsDisplayed()
            onNodeWithText(editShopName).assertIsDisplayed()

            onNodeWithText(PRODUCT_NAME).assertIsDisplayed()
            onNodeWithText(DESCRIPTION).assertIsDisplayed()
            onNodeWithText(SHOP).assertIsDisplayed()
        }
    }

    private fun getState() = ProductManagerViewState(
        images = listOf(),
        productName = PRODUCT_NAME,
        description = DESCRIPTION,
        shop = SHOP,
        type = HateRateType.RATE,
        draftProduct = null,
        setDescription = {},
        setName = {},
        setShop = {},
        productSaved = false,
        onDeleteImageClicked = {},
        onAddImageFromGalleryClicked = {},
        onAddCameraPictureClicked = {},
        onQuit = {},
        onProductDone = {},
        getCameraImageFileUri = {
            CameraTakePictureData.empty()
        },
        onTypeClicked = {},
        forceQuit = false,
        onForceQuit = {},
        showAlertDialog = false,
        onShowAlertDialog = {},
        trackOnScreenStart = {}
    )
}
