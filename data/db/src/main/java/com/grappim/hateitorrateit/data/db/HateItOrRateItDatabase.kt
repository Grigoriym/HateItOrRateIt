package com.grappim.hateitorrateit.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.grappim.hateitorrateit.data.db.converters.DateTimeConverter
import com.grappim.hateitorrateit.data.db.converters.HateRateTypeConverter
import com.grappim.hateitorrateit.data.db.dao.BackupImagesDao
import com.grappim.hateitorrateit.data.db.dao.DatabaseDao
import com.grappim.hateitorrateit.data.db.dao.ProductsDao
import com.grappim.hateitorrateit.data.db.entities.BackupProductImageDataEntity
import com.grappim.hateitorrateit.data.db.entities.ProductEntity
import com.grappim.hateitorrateit.data.db.entities.ProductImageDataEntity

@Database(
    entities = [
        ProductEntity::class,
        ProductImageDataEntity::class,
        BackupProductImageDataEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    DateTimeConverter::class,
    HateRateTypeConverter::class
)
abstract class HateItOrRateItDatabase : RoomDatabase() {

    abstract fun productsDao(): ProductsDao
    abstract fun databaseDao(): DatabaseDao
    abstract fun backupImagesDao(): BackupImagesDao
}
