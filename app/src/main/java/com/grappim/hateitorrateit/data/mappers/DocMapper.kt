package com.grappim.hateitorrateit.data.mappers

import com.grappim.hateitorrateit.data.db.DocumentEntity
import com.grappim.hateitorrateit.data.db.DocumentFileDataEntity
import com.grappim.hateitorrateit.data.db.DocumentWithFilesEntity
import com.grappim.hateitorrateit.domain.Document
import com.grappim.hateitorrateit.domain.DocumentFileData
import com.grappim.hateitorrateit.model.CreateDocument

fun List<DocumentWithFilesEntity>.toDocument() =
    this.map { documentWithFilesEntity ->
        documentWithFilesEntity.documentEntity.toDocument(documentWithFilesEntity.files)
    }

fun DocumentEntity.toDocument(
    fileDataList: List<DocumentFileDataEntity>?
): Document =
    Document(
        id = this.documentId,
        name = this.name,
        filesUri = fileDataList?.map { documentFileDataEntity ->
            DocumentFileData(
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
    )

fun CreateDocument.toEntity(): DocumentEntity =
    DocumentEntity(
        documentId = this.id,
        name = this.name,
        createdDate = this.createdDate,
        documentFolderName = this.documentFolderName,
        description = this.description,
        shop = this.shop,
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