package com.grappim.hateitorrateit.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.grappim.hateitorrateit.strings.RString
import com.grappim.hateitorrateit.ui.screens.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductFlowsE2ETest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private val robot = AppRobot(rule)

    @Test
    fun createdProductAppearsOnHome() {
        robot.createProduct(name = "Espresso beans", shop = "Corner Shop")

        rule.onNodeWithText("Espresso beans").assertIsDisplayed()
    }

    @Test
    fun productDetailsShowWhatWasEntered() {
        robot.createProduct(
            name = "Oat milk",
            shop = "Market",
            description = "Too sweet"
        )

        robot.openProduct("Oat milk")

        rule.onNodeWithText("Market").assertIsDisplayed()
        rule.onNodeWithText("Too sweet").assertIsDisplayed()
    }

    @Test
    fun editedProductShowsNewValues() {
        robot.createProduct(name = "Old name", shop = "Old shop")
        robot.openProduct("Old name")

        robot.clickEdit()
        robot.fillProduct(name = "New name", shop = "New shop")
        robot.clickSave()

        robot.waitForText("New name")
        rule.onNodeWithText("New shop").assertIsDisplayed()

        robot.pressBack()
        robot.waitForHome()
        rule.onNodeWithText("New name").assertIsDisplayed()
        rule.onNodeWithText("Old name").assertDoesNotExist()
    }

    @Test
    fun deletedProductDisappearsFromHome() {
        robot.createProduct(name = "To be deleted")
        robot.openProduct("To be deleted")

        robot.clickDeleteAndConfirm()

        robot.waitForHome()
        robot.waitForTextGone("To be deleted")
    }

    @Test
    fun searchShowsOnlyMatchingProducts() {
        robot.createProduct(name = "Green tea")
        robot.createProduct(name = "Black coffee")

        robot.search("tea")

        robot.waitForTextGone("Black coffee")
        rule.onNodeWithText("Green tea").assertIsDisplayed()
    }

    @Test
    fun searchWithApostropheDoesNotCrash() {
        robot.createProduct(name = "Sam's cookies")

        robot.search("Sam's")

        rule.onNodeWithText("Sam's cookies").assertIsDisplayed()
    }

    @Test
    fun discardingNewProductDoesNotSaveIt() {
        robot.openNewProduct()
        robot.fillProduct(name = "Never saved")

        robot.pressBack()
        robot.waitForText(robot.string(RString.proceed))
        rule.onNodeWithText(robot.string(RString.proceed)).performClick()

        robot.waitForHome()
        rule.onNodeWithText("Never saved").assertDoesNotExist()
    }

    @Test
    fun productWithoutNameIsNotCreated() {
        robot.openNewProduct()

        rule.onNodeWithText(robot.string(RString.create)).performClick()

        robot.waitForText(robot.string(RString.set_name))
        rule.onNodeWithText(robot.string(RString.create)).assertIsDisplayed()
    }
}
