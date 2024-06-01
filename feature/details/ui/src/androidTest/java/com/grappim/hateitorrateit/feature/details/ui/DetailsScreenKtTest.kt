package com.grappim.hateitorrateit.feature.details.ui

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.uikit.widgets.PLATO_PLACEHOLDER_IMAGE_TAG
import com.grappim.hateitorrateit.uikit.widgets.PLATO_TOP_BAR_TAG
import com.grappim.hateitorrateit.uikit.widgets.PROGRESS_INDICATOR_TAG
import com.grappim.hateitorrateit.utils.ui.PlatoIconType
import org.junit.Rule
import org.junit.Test

class DetailsScreenKtTest {

    private val name = "test_name"
    private val description = "test_description"
    private val shop = "test_shop"
    private val createdDate = "2023.12.28"

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun isLoading_true_verify_progress_indicator_is_visible_content_is_not_visible() {
        composeTestRule.run {
            setContent {
                DetailsScreen(
                    state = getState().copy(isLoading = true),
                    goBack = { },
                    onImageClicked = { _, _ -> },
                    onEditClicked = { _ -> },
                    isFromEdit = false
                )
            }

            onNodeWithTag(PROGRESS_INDICATOR_TAG).assertIsDisplayed()
            onNodeWithTag(DETAILS_SCREEN_CONTENT_TAG).assertDoesNotExist()
        }
    }

    @Test
    fun isLoading_false_verify_default_content_is_visible_progress_indicator_is_not_visible() {
        composeTestRule.run {
            setContent {
                DetailsScreen(
                    state = getState().copy(
                        isLoading = false,
                        type = HateRateType.HATE
                    ),
                    goBack = { },
                    onImageClicked = { _, _ -> },
                    onEditClicked = { _ -> },
                    isFromEdit = false
                )
            }
            onNodeWithTag(PROGRESS_INDICATOR_TAG).assertDoesNotExist()
            onNodeWithTag(DETAILS_SCREEN_CONTENT_TAG).assertIsDisplayed()

            onNodeWithTag(DETAILS_DEMONSTRATION_CONTENT_TAG).assertIsDisplayed()

            onNodeWithTag(DETAILS_TOP_APP_BAR_TAG).assertIsDisplayed()
            onNodeWithTag(PLATO_TOP_BAR_TAG).assertIsDisplayed()

            onNodeWithTag(PlatoIconType.ArrowBack.testTag).assertIsDisplayed()
            onNodeWithTag(PlatoIconType.Edit.testTag).assertIsDisplayed()
            onNodeWithTag(PlatoIconType.Delete.testTag).assertIsDisplayed()

            onNodeWithText(name).assertIsDisplayed()
            onNodeWithText(description).assertIsDisplayed()
            onNodeWithText(shop).assertIsDisplayed()
            onNodeWithText(createdDate).assertIsDisplayed()

            onNodeWithTag(PlatoIconType.Hate.testTag).assertIsDisplayed()

            onNodeWithTag(PLATO_PLACEHOLDER_IMAGE_TAG).assertIsDisplayed()
        }
    }

    private fun getState() = DetailsViewState(
        productId = "",
        name = name,
        description = description,
        shop = shop,
        createdDate = createdDate,
        productFolderName = "",
        images = emptyList(),
        type = null,
        isLoading = false,
        showAlertDialog = false,
        onShowAlertDialog = {},
        onDeleteProduct = {},
        productDeleted = false,
        onDeleteProductConfirm = {},
        updateProduct = {},
        trackScreenStart = {},
        trackEditButtonClicked = {},
        appSettingsIntent = Intent(),
        clearShareImageIntent = {},
        onShareImageClicked = {},
        onShowPermissionsAlertDialog = { _, _ -> },
        resetSaveFileToGalleryState = {},
        saveFileToGallery = {},
        setCurrentDisplayedImageIndex = {},
        setSnackbarMessage = {}
    )
}
