package com.grappim.hateitorrateit.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.grappim.hateitorrateit.data.db.entities.BackupProductImageDataEntity

@Dao
interface BackupImagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<BackupProductImageDataEntity>)

    @Delete
    suspend fun delete(list: List<BackupProductImageDataEntity>)

    @Query("DELETE FROM backup_product_image_data_entity WHERE productId =:productId")
    suspend fun deleteImagesByProductId(productId: Long)

    @Query("SELECT * FROM backup_product_image_data_entity WHERE productId =:productId")
    suspend fun getAllImagesByProductId(productId: Long): List<BackupProductImageDataEntity>
}
