package com.grappim.hateitorrateit.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.grappim.hateitorrateit.data.db.entities.DocumentEntity
import com.grappim.hateitorrateit.data.db.entities.DocumentFileDataEntity

@[Database(
    entities = [
        DocumentEntity::class,
        DocumentFileDataEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
TypeConverters(
    DateTimeConverter::class,
    HateRateTypeConverter::class
)]
abstract class HateItOrRateItDatabase : RoomDatabase() {

    abstract fun documentsDao(): DocumentsDao
    abstract fun databaseDao(): DatabaseDao
}
