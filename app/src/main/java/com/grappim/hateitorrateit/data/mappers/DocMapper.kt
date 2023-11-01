package com.grappim.hateitorrateit.data.mappers

import com.grappim.domain.Document
import com.grappim.domain.ProductFileData
import com.grappim.hateitorrateit.data.db.entities.DocumentEntity
import com.grappim.hateitorrateit.data.db.entities.DocumentFileDataEntity
import com.grappim.hateitorrateit.data.db.entities.DocumentWithFilesEntity
import com.grappim.hateitorrateit.model.CreateDocument

fun DocumentWithFilesEntity.toDocument() =
    this.documentEntity.toDocument(this.files)

fun DocumentEntity.toDocument(
    fileDataList: List<DocumentFileDataEntity>?
): Document =
    Document(
        id = this.documentId,
        name = this.name,
        filesUri = fileDataList?.map { documentFileDataEntity ->
            ProductFileData(
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

fun List<ProductFileData>.toEntities(docId: Long) =
    this.map { it.toEntity(docId) }

fun ProductFileData.toEntity(docId: Long): DocumentFileDataEntity =
    DocumentFileDataEntity(
        name = this.name,
        mimeType = this.mimeType,
        uriPath = this.uriPath,
        uriString = this.uriString,
        size = this.size,
        md5 = this.md5,
        documentId = docId
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