package com.grappim.hateitorrateit.data.db.wrapper

import com.grappim.hateitorrateit.data.db.HateItOrRateItDatabase
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DatabaseWrapperImplTest {
    private val db = mockk<HateItOrRateItDatabase>(relaxed = true)

    private lateinit var wrapper: DatabaseWrapper

    @Before
    fun setup() {
        wrapper = DatabaseWrapperImpl(
            db = db,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun `productsDao should call db productsDao`() {
        wrapper.productsDao

        verify { db.productsDao() }
    }

    @Test
    fun `databaseDao should call db databaseDao`() {
        wrapper.databaseDao

        verify { db.databaseDao() }
    }

    @Test
    fun `backupImagesDao should call db backupImagesDao`() {
        wrapper.backupImagesDao

        verify { db.backupImagesDao() }
    }

    @Test
    fun `clearAllTables should call db clearAllTables`() = runTest {
        wrapper.clearAllTables()

        verify { db.clearAllTables() }
    }

    @Test
    fun `close should call db close`() = runTest {
        wrapper.close()

        verify { db.close() }
    }
}
