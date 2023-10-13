package com.grappim.hateitorrateit.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "document_file_data_table"
)
data class DocumentFileDataEntity(
    @PrimaryKey(autoGenerate = true)
    val fileId: Long = 0,

    val documentId: Long,

    val name: String,
    val mimeType: String,
    val size: Long,

    val uriPath: String,
    val uriString: String,

    val md5: String
)