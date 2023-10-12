package com.grappim.hateitorrateit.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@[Database(
    entities = [
        DocumentEntity::class,
        DocumentFileDataEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
TypeConverters(
    DateTimeConverter::class,
)]
abstract class HateItOrRateItDatabase : RoomDatabase() {

    abstract fun documentsDao(): DocumentsDao
    abstract fun databaseDao(): DatabaseDao
}