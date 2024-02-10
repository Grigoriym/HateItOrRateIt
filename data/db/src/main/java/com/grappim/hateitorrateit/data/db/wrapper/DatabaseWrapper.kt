package com.grappim.hateitorrateit.data.db.wrapper

import com.grappim.hateitorrateit.data.db.dao.BackupImagesDao
import com.grappim.hateitorrateit.data.db.dao.DatabaseDao
import com.grappim.hateitorrateit.data.db.dao.ProductsDao

interface DatabaseWrapper {
    val productsDao: ProductsDao

    val databaseDao: DatabaseDao

    val backupImagesDao: BackupImagesDao

    suspend fun clearAllTables()

    fun close()
}
