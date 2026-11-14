package com.grappim.hateitorrateit.e2e

import androidx.annotation.StringRes
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.grappim.hateitorrateit.strings.RString
import com.grappim.hateitorrateit.ui.screens.main.ADD_PRODUCT_FAB_TAG
import com.grappim.hateitorrateit.ui.screens.main.MainActivity
import com.grappim.hateitorrateit.uikit.icons.PlatoIconType

private const val TIMEOUT_MS = 10_000L

@OptIn(ExperimentalTestApi::class)
class AppRobot(
    private val rule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>
) {

    fun string(@StringRes id: Int): String = rule.activity.getString(id)

    fun waitForText(text: String) {
        rule.waitUntilAtLeastOneExists(hasText(text), TIMEOUT_MS)
    }

    fun waitForTextGone(text: String) {
        rule.waitUntilDoesNotExist(hasText(text), TIMEOUT_MS)
    }

    fun waitForHome() {
        rule.waitUntilAtLeastOneExists(hasText(string(RString.home)), TIMEOUT_MS)
    }

    fun openNewProduct() {
        waitForHome()
        rule.onNodeWithTag(ADD_PRODUCT_FAB_TAG).performClick()
        waitForText(string(RString.create))
    }

    fun fillProduct(name: String, shop: String = "", description: String = "") {
        textField(RString.name_obligatory).performTextReplacement(name)
        if (shop.isNotEmpty()) textField(RString.shop).performTextReplacement(shop)
        if (description.isNotEmpty()) {
            textField(RString.description).performTextReplacement(description)
        }
    }

    fun createProduct(name: String, shop: String = "", description: String = "") {
        openNewProduct()
        fillProduct(name, shop, description)
        rule.onNodeWithText(string(RString.create)).performClick()
        waitForHome()
        waitForText(name)
    }

    fun openProduct(name: String) {
        rule.onNodeWithText(name).performClick()
        rule.waitUntilAtLeastOneExists(
            hasTestTag(PlatoIconType.Edit.imageVector.name),
            TIMEOUT_MS
        )
    }

    fun clickEdit() {
        rule.onNodeWithTag(PlatoIconType.Edit.imageVector.name).performClick()
        waitForText(string(RString.save))
    }

    fun clickSave() {
        rule.onNodeWithText(string(RString.save)).performClick()
    }

    fun clickDeleteAndConfirm() {
        rule.onNodeWithTag(PlatoIconType.Delete.imageVector.name).performClick()
        waitForText(string(RString.yes))
        rule.onNodeWithText(string(RString.yes)).performClick()
    }

    fun search(query: String) {
        waitForHome()
        rule.onNode(hasSetTextAction()).performTextInput(query)
    }

    fun pressBack() {
        rule.runOnUiThread { rule.activity.onBackPressedDispatcher.onBackPressed() }
    }

    fun textField(@StringRes label: Int) = rule.onNode(hasSetTextAction() and hasText(string(label), substring = true))
}
