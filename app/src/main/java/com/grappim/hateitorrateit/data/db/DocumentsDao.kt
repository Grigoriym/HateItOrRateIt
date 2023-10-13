package com.grappim.hateitorrateit.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(documentEntity: DocumentEntity): Long

    @[Transaction Query("SELECT * FROM document_table")]
    fun getAllDocsFlow(): Flow<List<DocumentWithFilesEntity>>

    @RawQuery
    fun getAllDocsByQueryFlow(query: SupportSQLiteQuery): Flow<List<DocumentWithFilesEntity>>

    @Query("SELECT * FROM document_table " +
            "WHERE name LIKE '%' || :query || '%' " +
            "OR shop LIKE '%' || :query || '%'")
    fun getAllDocsByQueryFlow(query: String): Flow<List<DocumentWithFilesEntity>>

    @[Transaction Query("SELECT * FROM document_table WHERE documentId = :id LIMIT 1")]
    suspend fun getDocById(id: Long): DocumentWithFilesEntity

    @Query("DELETE FROM document_table WHERE documentId = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE document_table SET name=:name, description=:description, shop=:shop WHERE documentId=:id")
    suspend fun updateDoc(
        id: Long,
        name: String,
        description: String,
        shop: String,
    )

    @Transaction
    suspend fun deleteDocumentWithData(id: Long) {
        deleteById(id)
        removeDocumentFileDataById(id)
    }

    @Transaction
    suspend fun updateDocumentAndFiles(
        documentEntity: DocumentEntity,
        list: List<DocumentFileDataEntity>
    ) {
        update(documentEntity)
        insertFiles(list)
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(documentEntity: DocumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(list: List<DocumentFileDataEntity>)

    @Query("DELETE FROM document_file_data_table WHERE documentId = :id")
    suspend fun removeDocumentFileDataById(id: Long)
}