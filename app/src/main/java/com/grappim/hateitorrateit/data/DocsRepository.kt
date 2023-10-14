package com.grappim.hateitorrateit.data

import com.grappim.hateitorrateit.core.HateRateType
import com.grappim.hateitorrateit.data.db.entities.DocumentEntity
import com.grappim.hateitorrateit.data.db.DocumentsDao
import com.grappim.hateitorrateit.data.mappers.toDocument
import com.grappim.hateitorrateit.data.mappers.toEntity
import com.grappim.hateitorrateit.data.mappers.toFileDataEntityList
import com.grappim.hateitorrateit.domain.Document
import com.grappim.hateitorrateit.model.CreateDocument
import com.grappim.hateitorrateit.utils.DateTimeUtils
import com.grappim.hateitorrateit.utils.DraftDocument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocsRepository @Inject constructor(
    private val dateTimeUtils: DateTimeUtils,
    private val documentsDao: DocumentsDao,
) {

    suspend fun getDocById(id: Long): Document {
        val entity = documentsDao.getDocById(id)
        val domain = entity.documentEntity.toDocument(entity.files)
        return domain
    }

    suspend fun updateDoc(
        id: Long,
        name: String,
        description: String,
        shop: String,
    ) {
        documentsDao.updateDoc(id, name, description, shop)
    }

    suspend fun addDraftDocument(): DraftDocument {
        val nowDate = dateTimeUtils.getDateTimeUTCNow()
        val formattedDate = dateTimeUtils.formatToDemonstrate(nowDate)
        val id = documentsDao.insert(
            DocumentEntity(
                name = formattedDate,
                createdDate = nowDate,
                documentFolderName = "",
                description = "",
                shop = "",
                type = HateRateType.HATE,
            )
        )
        val folderDate = dateTimeUtils.formatToGDrive(nowDate)
        val folderName = "${id}_${folderDate}"
        return DraftDocument(
            id = id,
            date = nowDate,
            folderName = folderName,
            type = HateRateType.HATE,
        )
    }

    fun getAllDocsFlow(query: String): Flow<List<Document>> =
        if (query.isEmpty()) {
            documentsDao.getAllDocsFlow()
        } else {
            documentsDao.getAllDocsByQueryFlow(query)
        }.map {
            it.filter { entity ->
                entity.files?.isNotEmpty() == true
            }
        }.map { it.toDocument() }

    suspend fun removeDocumentById(id: Long) {
        documentsDao.deleteDocumentWithData(id)
    }

    suspend fun addDocument(document: CreateDocument) {
        val entity = document.toEntity()
        val list = document.toFileDataEntityList()
        documentsDao.updateDocumentAndFiles(
            documentEntity = entity,
            list = list
        )
    }
}
