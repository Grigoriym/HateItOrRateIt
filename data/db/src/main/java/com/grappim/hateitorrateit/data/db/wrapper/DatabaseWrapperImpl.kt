package com.grappim.hateitorrateit.data.db.wrapper

import com.grappim.hateitorrateit.core.async.IoDispatcher
import com.grappim.hateitorrateit.data.db.HateItOrRateItDatabase
import com.grappim.hateitorrateit.data.db.dao.BackupImagesDao
import com.grappim.hateitorrateit.data.db.dao.DatabaseDao
import com.grappim.hateitorrateit.data.db.dao.ProductsDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseWrapperImpl @Inject constructor(
    private val db: HateItOrRateItDatabase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DatabaseWrapper {

    override val productsDao: ProductsDao get() = db.productsDao()

    override val databaseDao: DatabaseDao get() = db.databaseDao()

    override val backupImagesDao: BackupImagesDao = db.backupImagesDao()

    override suspend fun clearAllTables() = withContext(ioDispatcher) {
        db.clearAllTables()
    }

    override fun close() {
        db.close()
    }
}
