package com.grappim.hateitorrateit.utils.ui

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class SnackbarDelegateImplTest {

    private lateinit var snackbarDelegate: SnackbarDelegateImpl

    @Before
    fun setUp() {
        snackbarDelegate = SnackbarDelegateImpl()
    }

    @Test
    fun `showSnackbar should send message to snackBarMessage flow`() = runTest {
        val testMessage = NativeText.Simple("Hello, World!")

        snackbarDelegate.snackBarMessage.test {
            snackbarDelegate.showSnackbar(testMessage)

            val receivedMessage = awaitItem()
            assertEquals(testMessage, receivedMessage)

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `showSnackbar multiple times should send multiple messages to flow`() = runTest {
        val message1 = NativeText.Simple("First message")
        val message2 = NativeText.Simple("second one")
        val message3 = NativeText.Simple("Third message")

        snackbarDelegate.snackBarMessage.test {
            snackbarDelegate.showSnackbar(message1)
            assertEquals(message1, awaitItem())

            snackbarDelegate.showSnackbar(message2)
            assertEquals(message2, awaitItem())

            snackbarDelegate.showSnackbar(message3)
            assertEquals(message3, awaitItem())

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `snackBarMessage flow should not emit before showSnackbar is called`() = runTest {
        snackbarDelegate.snackBarMessage.test {
            expectNoEvents()

            val testMessage = NativeText.Simple("Test")
            snackbarDelegate.showSnackbar(testMessage)
            assertEquals(testMessage, awaitItem())

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `calling showSnackbar consecutively quickly works as expected`() = runTest {
        val message1 = NativeText.Simple("Quick 1")
        val message2 = NativeText.Simple("Quick 2")

        snackbarDelegate.snackBarMessage.test {
            snackbarDelegate.showSnackbar(message1)
            snackbarDelegate.showSnackbar(message2)

            assertEquals(message1, awaitItem())
            assertEquals(message2, awaitItem())

            ensureAllEventsConsumed()
        }
    }
}
