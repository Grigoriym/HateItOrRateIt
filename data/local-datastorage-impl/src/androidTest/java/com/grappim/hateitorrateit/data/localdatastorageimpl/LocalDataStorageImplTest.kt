package com.grappim.hateitorrateit.data.localdatastorageimpl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertEquals

class LocalDataStorageImplTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder()
        .assureDeletion().build()

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var testDataStore: DataStore<Preferences>

    private lateinit var localDataStorage: LocalDataStorage

    @Before
    fun setup() = runTest {
        testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = {
                tmpFolder.newFile("user_preferences_test.preferences_pb")
            }
        )

        localDataStorage = LocalDataStorageImpl(testDataStore)
    }

    @Test
    fun initial_state_of_type_should_be_correct() = runTest {
        localDataStorage.typeFlow.test {
            assertEquals(awaitItem(), com.grappim.hateitorrateit.data.repoapi.models.HateRateType.HATE)
        }
    }

    @Test
    fun initial_state_of_crashesCollectionEnabled_should_be_correct() = runTest {
        localDataStorage.crashesCollectionEnabled.test {
            assertEquals(awaitItem(), true)
        }
    }

    @Test
    fun initial_state_of_analyticsCollectionEnabled_should_be_correct() = runTest {
        localDataStorage.analyticsCollectionEnabled.test {
            assertEquals(awaitItem(), true)
        }
    }

    @Test
    fun when_changing_the_type_the_flow_should_emit_correct_value() = runTest {
        localDataStorage.typeFlow.test {
            assertEquals(awaitItem(), com.grappim.hateitorrateit.data.repoapi.models.HateRateType.HATE)

            localDataStorage.changeTypeTo(com.grappim.hateitorrateit.data.repoapi.models.HateRateType.RATE)

            assertEquals(awaitItem(), com.grappim.hateitorrateit.data.repoapi.models.HateRateType.RATE)
        }
    }

    @Test
    fun when_setCrashesCollectionEnabled_should_emit_correct_value() = runTest {
        localDataStorage.crashesCollectionEnabled.test {
            assertEquals(awaitItem(), true)

            localDataStorage.setCrashesCollectionEnabled(false)

            assertEquals(awaitItem(), false)
        }
    }

    @Test
    fun when_setAnalyticsCollectionEnabled_should_emit_correct_value() = runTest {
        localDataStorage.analyticsCollectionEnabled.test {
            assertEquals(awaitItem(), true)

            localDataStorage.setAnalyticsCollectionEnabled(false)

            assertEquals(awaitItem(), false)
        }
    }
}
