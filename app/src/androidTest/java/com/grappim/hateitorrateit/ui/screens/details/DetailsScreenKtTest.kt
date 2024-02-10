package com.grappim.hateitorrateit.ui.screens.details

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.ui.utils.PlatoIconType
import com.grappim.hateitorrateit.ui.widgets.PLATO_PLACEHOLDER_IMAGE_TAG
import com.grappim.hateitorrateit.ui.widgets.PLATO_TOP_BAR_TAG
import com.grappim.hateitorrateit.ui.widgets.PROGRESS_INDICATOR_TAG
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val NAME = "test_name"
private const val DESCRIPTION = "test_description"
private const val SHOP = "test_shop"
private const val CREATED_DATE = "2023.12.28"

class DetailsScreenKtTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        composeTestRule.activity.apply {
        }
    }

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

            onNodeWithText(NAME).assertIsDisplayed()
            onNodeWithText(DESCRIPTION).assertIsDisplayed()
            onNodeWithText(SHOP).assertIsDisplayed()
            onNodeWithText(CREATED_DATE).assertIsDisplayed()

            onNodeWithTag(PlatoIconType.ThumbDown.testTag).assertIsDisplayed()

            onNodeWithTag(PLATO_PLACEHOLDER_IMAGE_TAG).assertIsDisplayed()
        }
    }

    private fun getState() = DetailsViewState(
        productId = "",
        name = NAME,
        description = DESCRIPTION,
        shop = SHOP,
        createdDate = CREATED_DATE,
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
        trackEditButtonClicked = {}
    )
}
