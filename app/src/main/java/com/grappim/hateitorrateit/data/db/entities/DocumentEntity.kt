package com.grappim.hateitorrateit.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grappim.domain.HateRateType
import java.time.OffsetDateTime

@Entity(
    tableName = "document_table"
)
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val documentId: Long = 0,
    val name: String,
    val createdDate: OffsetDateTime,
    val documentFolderName: String,
    val description: String,
    val shop: String,
    val type: HateRateType,
)
