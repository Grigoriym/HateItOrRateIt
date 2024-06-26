package com.grappim.hateitorrateit.feature.settings.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.feature.settings.ui.screen.SettingsScreen
import com.grappim.hateitorrateit.feature.settings.ui.screen.SettingsViewState
import com.grappim.hateitorrateit.uikit.icons.PlatoIconType
import com.grappim.hateitorrateit.uikit.widgets.PLATO_ALERT_DIALOG_TAG
import com.grappim.hateitorrateit.uikit.widgets.PLATO_LOADING_DIALOG_TAG
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsNavScreenScreenKtTest {

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

            onAllNodesWithTag(
                PlatoIconType.HighlightOff.testTag,
                useUnmergedTree = true
            ).assertCountEquals(2)
            onNodeWithTag(PlatoIconType.Rate.testTag, useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun isLoading_true_alert_false_verify_that_loading_dialog_is_displayed() {
        composeTestRule.run {
            setContent {
                SettingsScreen(
                    goBack = {},
                    state = getState().copy(isLoading = true)
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
                    state = getState().copy(showAlertDialog = true)
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
                    state = getState().copy(isCrashesCollectionEnabled = true)
                )
            }
            onNodeWithTag(
                PlatoIconType.CheckCircleOutline.testTag,
                useUnmergedTree = true
            ).assertIsDisplayed()
        }
    }

    @Test
    fun isLoading_false_alert_false_analytics_enabled_verify_that_analytics_toggle_is_checked() {
        composeTestRule.run {
            setContent {
                SettingsScreen(
                    goBack = {},
                    state = getState().copy(isAnalyticsCollectionEnabled = true)
                )
            }
            onNodeWithTag(
                PlatoIconType.CheckCircleOutline.testTag,
                useUnmergedTree = true
            ).assertIsDisplayed()
        }
    }

    @Test
    fun isLoading_false_alert_false_crashlytics_disabled_verify_that_crashlytics_toggle_is_unchecked() {
        composeTestRule.run {
            setContent {
                SettingsScreen(
                    goBack = {},
                    state = getState().copy(
                        isCrashesCollectionEnabled = false,
                        isAnalyticsCollectionEnabled = true
                    )
                )
            }
            onNodeWithTag(
                PlatoIconType.HighlightOff.testTag,
                useUnmergedTree = true
            ).assertIsDisplayed()
        }
    }

    @Test
    fun isLoading_false_alert_false_analytics_disabled_verify_that_analytics_toggle_is_unchecked() {
        composeTestRule.run {
            setContent {
                SettingsScreen(
                    goBack = {},
                    state = getState().copy(
                        isCrashesCollectionEnabled = true,
                        isAnalyticsCollectionEnabled = false
                    )
                )
            }
            onNodeWithTag(
                PlatoIconType.HighlightOff.testTag,
                useUnmergedTree = true
            ).assertIsDisplayed()
        }
    }

    private fun getState() = SettingsViewState(
        isLoading = false,
        type = HateRateType.RATE,
        showAlertDialog = false,
        isCrashesCollectionEnabled = false,
        setNewType = {},
        onClearDataClicked = {},
        onAlertDialogConfirmButtonClicked = {},
        onDismissDialog = {},
        onCrashlyticsToggle = {},
        trackScreenStart = {},
        isAnalyticsCollectionEnabled = false,
        onAnalyticsToggle = {},
        onDarkThemeConfigClicked = {},
        localeOptions = mutableMapOf(),
        appInfo = "asd"
    )
}
