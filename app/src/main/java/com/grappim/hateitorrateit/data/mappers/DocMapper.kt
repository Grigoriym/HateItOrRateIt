package com.grappim.hateitorrateit.data.mappers

import com.grappim.hateitorrateit.data.db.DocumentEntity
import com.grappim.hateitorrateit.data.db.DocumentFileDataEntity
import com.grappim.hateitorrateit.domain.Document
import com.grappim.hateitorrateit.domain.DocumentFileData
import com.grappim.hateitorrateit.model.CreateDocument

fun DocumentEntity.toDocument(
    fileDataList: List<DocumentFileDataEntity>?
): Document =
    Document(
        id = this.documentId,
        name = this.name,
        filesUri = fileDataList?.map { dto ->
            DocumentFileData(
                name = dto.name,
                mimeType = dto.mimeType,
                uriPath = dto.uriPath,
                uriString = dto.uriString,
                size = dto.size,
                previewUriPath = dto.previewUriPath,
                previewUriString = dto.previewUriString,
                md5 = dto.md5
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
            previewUriString = it.previewUriString,
            previewUriPath = it.previewUriPath,
            md5 = it.md5
        )
    }