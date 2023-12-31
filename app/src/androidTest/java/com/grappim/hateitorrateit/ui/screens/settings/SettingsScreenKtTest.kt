package com.grappim.hateitorrateit.ui.screens.settings

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.ui.R
import com.grappim.hateitorrateit.ui.utils.PlatoIconType
import com.grappim.hateitorrateit.ui.widgets.PLATO_ALERT_DIALOG_TAG
import com.grappim.hateitorrateit.ui.widgets.PLATO_LOADING_DIALOG_TAG
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsScreenKtTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var settingsTitle: String
    private lateinit var backButtonContentDescription: String
    private lateinit var clearDataText: String
    private lateinit var typeText: String
    private lateinit var crashlyticsText: String
    private lateinit var crashlyticsSubtitleText: String

    @Before
    fun setup() {
        composeTestRule.activity.apply {
            settingsTitle = getString(R.string.settings)
            backButtonContentDescription = getString(R.string.content_description_back_button)
            clearDataText = getString(R.string.clear_data)
            typeText = getString(R.string.default_type)
            crashlyticsText = getString(R.string.toggle_crashlytics)
            crashlyticsSubtitleText = getString(R.string.crashlytics_settings_subtitle)
        }
    }

    @Test
    fun isLoading_false_alert_false_verify_that_screen_is_correctly_displayed() {
        composeTestRule.run {
            setContent {
                SettingsScreen(
                    goBack = {},
                    state = getState()
                )
            }

            onNodeWithText(settingsTitle).assertIsDisplayed()
            onNodeWithTag(PLATO_LOADING_DIALOG_TAG).assertDoesNotExist()

            onNodeWithContentDescription(backButtonContentDescription).assertIsDisplayed()
            onNodeWithTag(PLATO_ALERT_DIALOG_TAG).assertDoesNotExist()

            onNodeWithText(clearDataText).assertIsDisplayed()
            onNodeWithText(typeText).assertIsDisplayed()
            onNodeWithText(crashlyticsText).assertIsDisplayed()
            onNodeWithText(crashlyticsSubtitleText).assertIsDisplayed()

            onNodeWithTag(PlatoIconType.HighlightOff.testTag, useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag(PlatoIconType.ThumbUp.testTag, useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun isLoading_true_alert_false_verify_that_loading_dialog_is_displayed() {
        composeTestRule.run {
            setContent {
                SettingsScreen(
                    goBack = {},
                    state = getState().safeCopy(isLoading = true)
                )
            }

            onNodeWithTag(PLATO_LOADING_DIALOG_TAG).assertIsDisplayed()
        }
    }

    @Test
    fun isLoading_false_alert_true_verify_that_alert_dialog_is_displayed() {
        composeTestRule.run {
            setContent {
                SettingsScreen(
                    goBack = {},
                    state = getState().safeCopy(showAlertDialog = true)
                )
            }

            onNodeWithTag(PLATO_ALERT_DIALOG_TAG).assertIsDisplayed()
        }
    }

    @Test
    fun isLoading_false_alert_false_crashlytics_enabled_verify_that_crashlytics_toggle_is_checked() {
        composeTestRule.run {
            setContent {
                SettingsScreen(
                    goBack = {},
                    state = getState().safeCopy(isCrashesCollectionEnabled = true)
                )
            }
            onNodeWithTag(CROSSFADE_TAG, useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag(PlatoIconType.CheckCircleOutline.testTag, useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun isLoading_false_alert_false_crashlytics_disabled_verify_that_crashlytics_toggle_is_unchecked() {
        composeTestRule.run {
            setContent {
                SettingsScreen(
                    goBack = {},
                    state = getState().safeCopy(isCrashesCollectionEnabled = false)
                )
            }
            onNodeWithTag(CROSSFADE_TAG, useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag(PlatoIconType.HighlightOff.testTag, useUnmergedTree = true).assertIsDisplayed()
        }
    }

    private fun getState() = SettingsViewState(
        isLoading = false,
        type = HateRateType.RATE,
        showAlertDialog = false,
        isCrashesCollectionEnabled = false,
        setType = {},
        onClearDataClicked = {},
        onAlertDialogConfirmButtonClicked = {},
        onDismissDialog = {},
        onCrashlyticsToggle = {}
    )
}
