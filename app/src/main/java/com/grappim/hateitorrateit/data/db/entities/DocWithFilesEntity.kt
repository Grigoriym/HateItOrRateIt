package com.grappim.hateitorrateit.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.grappim.hateitorrateit.data.db.entities.DocumentEntity
import com.grappim.hateitorrateit.data.db.entities.DocumentFileDataEntity

data class DocumentWithFilesEntity(
    @Embedded val documentEntity: DocumentEntity,
    @Relation(
        parentColumn = "documentId",
        entityColumn = "documentId"
    )
    val files: List<DocumentFileDataEntity>?
)
