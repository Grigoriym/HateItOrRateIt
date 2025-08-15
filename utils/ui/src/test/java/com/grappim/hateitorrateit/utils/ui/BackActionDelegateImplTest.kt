package com.grappim.hateitorrateit.utils.ui

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class BackActionDelegateImplTest {

    private lateinit var backActionDelegate: BackActionDelegateImpl

    @Before
    fun setUp() {
        backActionDelegate = BackActionDelegateImpl()
    }

    @Test
    fun `triggerBackAction should send Unit to onBackAction flow`() = runTest {
        backActionDelegate.onBackAction.test {
            backActionDelegate.triggerBackAction()

            assertEquals(Unit, awaitItem())

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `triggerBackAction multiple times should send multiple Units to onBackAction flow`() =
        runTest {
            backActionDelegate.onBackAction.test {
                backActionDelegate.triggerBackAction()
                assertEquals(Unit, awaitItem())

                backActionDelegate.triggerBackAction()
                assertEquals(Unit, awaitItem())

                backActionDelegate.triggerBackAction()
                assertEquals(Unit, awaitItem())

                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `onBackAction flow should not emit anything before triggerBackAction is called`() =
        runTest {
            backActionDelegate.onBackAction.test {
                expectNoEvents()

                backActionDelegate.triggerBackAction()
                assertEquals(Unit, awaitItem())

                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `calling triggerBackAction consecutively quickly works as expected`() = runTest {
        backActionDelegate.onBackAction.test {
            backActionDelegate.triggerBackAction()
            backActionDelegate.triggerBackAction()
            backActionDelegate.triggerBackAction()

            assertEquals(Unit, awaitItem())
            assertEquals(Unit, awaitItem())
            assertEquals(Unit, awaitItem())

            ensureAllEventsConsumed()
        }
    }
}
