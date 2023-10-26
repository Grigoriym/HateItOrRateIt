package com.grappim.hateitorrateit.data.mappers

import com.grappim.hateitorrateit.data.db.entities.DocumentEntity
import com.grappim.hateitorrateit.data.db.entities.DocumentFileDataEntity
import com.grappim.hateitorrateit.data.db.entities.DocumentWithFilesEntity
import com.grappim.hateitorrateit.model.CreateDocument

fun DocumentWithFilesEntity.toDocument() =
    this.documentEntity.toDocument(this.files)

fun DocumentEntity.toDocument(
    fileDataList: List<DocumentFileDataEntity>?
): com.grappim.domain.Document =
    com.grappim.domain.Document(
        id = this.documentId,
        name = this.name,
        filesUri = fileDataList?.map { documentFileDataEntity ->
            com.grappim.domain.DocumentFileData(
                name = documentFileDataEntity.name,
                mimeType = documentFileDataEntity.mimeType,
                uriPath = documentFileDataEntity.uriPath,
                uriString = documentFileDataEntity.uriString,
                size = documentFileDataEntity.size,
                md5 = documentFileDataEntity.md5
            )
        } ?: emptyList(),
        createdDate = this.createdDate,
        documentFolderName = this.documentFolderName,
        description = this.description,
        shop = this.shop,
        type = this.type
    )

fun CreateDocument.toEntity(): DocumentEntity =
    DocumentEntity(
        documentId = this.id,
        name = this.name,
        createdDate = this.createdDate,
        documentFolderName = this.documentFolderName,
        description = this.description,
        shop = this.shop,
        type = this.type,
        isCreated = true,
    )

fun CreateDocument.toFileDataEntityList(): List<DocumentFileDataEntity> =
    filesUri.map {
        DocumentFileDataEntity(
            documentId = this.id,
            name = it.name,
            mimeType = it.mimeType,
            size = it.size,
            uriPath = it.uriPath,
            uriString = it.uriString,
            md5 = it.md5
        )
    }