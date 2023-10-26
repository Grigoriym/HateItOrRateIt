package com.grappim.hateitorrateit.data

import androidx.sqlite.db.SimpleSQLiteQuery
import com.grappim.domain.Document
import com.grappim.domain.HateRateType
import com.grappim.hateitorrateit.core.di.IoDispatcher
import com.grappim.hateitorrateit.data.db.DocumentsDao
import com.grappim.hateitorrateit.data.db.entities.DocumentEntity
import com.grappim.hateitorrateit.data.db.wrapWithPercentWildcards
import com.grappim.hateitorrateit.data.db.wrapWithSingleQuotes
import com.grappim.hateitorrateit.data.mappers.toDocument
import com.grappim.hateitorrateit.data.mappers.toEntity
import com.grappim.hateitorrateit.data.mappers.toFileDataEntityList
import com.grappim.hateitorrateit.data.storage.local.LocalDataStorage
import com.grappim.hateitorrateit.model.CreateDocument
import com.grappim.hateitorrateit.utils.DateTimeUtils
import com.grappim.hateitorrateit.utils.DraftDocument
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocsRepository @Inject constructor(
    private val dateTimeUtils: DateTimeUtils,
    private val documentsDao: DocumentsDao,
    private val localDataStorage: LocalDataStorage,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
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
        type: HateRateType,
    ) {
        documentsDao.updateDoc(id, name, description, shop, type)
    }

    suspend fun addDraftDocument(): DraftDocument {
        val nowDate = dateTimeUtils.getDateTimeUTCNow()
        val type = localDataStorage.typeFlow.first()
        val folderDate = dateTimeUtils.formatToGDrive(nowDate)
        val documentEntity = DocumentEntity(
            name = "",
            createdDate = nowDate,
            documentFolderName = "",
            description = "",
            shop = "",
            type = type,
        )

        val id = documentsDao.insert(documentEntity)
        val folderName = "${id}_${folderDate}"
        documentsDao.updateDocFolderName(folderName, id)
        return DraftDocument(
            id = id,
            date = nowDate,
            folderName = folderName,
            type = type,
        )
    }

    suspend fun getEmptyFiles() = documentsDao.getEmptyFiles()

    suspend fun deleteEmptyFiles() = documentsDao.deleteEmptyFiles()

    fun getAllDocsFlow(
        query: String,
        type: HateRateType?,
    ): Flow<List<Document>> = flow {
        emitAll(if (query.isEmpty() && type == null) {
            documentsDao.getAllDocsFlow()
        } else {
            val sqLiteQuery = buildSqlQuery(query, type)
            documentsDao.getAllDocsByRawQueryFlow(SimpleSQLiteQuery(sqLiteQuery))
        }.mapLatest { list ->
            list.map { it.toDocument() }
        })
    }

    private fun buildSqlQuery(
        query: String,
        type: HateRateType?
    ): String {
        val sqlQuery = StringBuilder("SELECT * FROM document_table ")
        if (query.isNotEmpty() || type != null) {
            sqlQuery.append("WHERE ")
        }
        if (query.isNotEmpty()) {
            sqlQuery.append(
                "(name LIKE ${
                    query.wrapWithPercentWildcards().wrapWithSingleQuotes()
                } "
            )
            sqlQuery.append(
                "OR shop LIKE ${
                    query.wrapWithPercentWildcards().wrapWithSingleQuotes()
                } "
            )
            sqlQuery.append(
                "OR description LIKE ${
                    query.wrapWithPercentWildcards().wrapWithSingleQuotes()
                }) "
            )
        }
        if (type != null) {
            if (query.isNotEmpty()) {
                sqlQuery.append("AND ")
            }
            sqlQuery.append("type=${type.name.wrapWithSingleQuotes()} ")
        }
        sqlQuery.append("AND isCreated=1 ")
        sqlQuery.append("ORDER BY createdDate DESC")
        Timber.d("sqlQuery: $sqlQuery")
        return sqlQuery.toString()
    }

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
