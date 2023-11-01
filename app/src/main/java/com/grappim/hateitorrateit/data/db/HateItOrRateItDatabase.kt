package com.grappim.hateitorrateit.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.grappim.hateitorrateit.data.db.entities.ProductEntity
import com.grappim.hateitorrateit.data.db.entities.ProductImageDataEntity

@[Database(
    entities = [
        ProductEntity::class,
        ProductImageDataEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
TypeConverters(
    DateTimeConverter::class,
    HateRateTypeConverter::class
)]
abstract class HateItOrRateItDatabase : RoomDatabase() {

    abstract fun productsDao(): ProductsDao
    abstract fun databaseDao(): DatabaseDao
}
