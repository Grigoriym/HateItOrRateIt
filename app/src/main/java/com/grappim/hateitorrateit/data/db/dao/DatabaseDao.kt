package com.grappim.hateitorrateit.data.db.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface DatabaseDao {
    @Query("DELETE FROM sqlite_sequence")
    suspend fun clearPrimaryKeyIndex()
}
