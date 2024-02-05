package com.grappim.hateitorrateit.data.db.wrapper

import com.grappim.hateitorrateit.data.db.dao.DatabaseDao
import com.grappim.hateitorrateit.data.db.dao.ProductsDao
import com.grappim.hateitorrateit.data.db.dao.BackupImagesDao

interface DatabaseWrapper {
    val productsDao: ProductsDao

    val databaseDao: DatabaseDao

    val backupImagesDao: BackupImagesDao

    suspend fun clearAllTables()

    fun close()
}
