package com.grappim.hateitorrateit.model

import com.grappim.domain.Document
import com.grappim.hateitorrateit.core.di.IoDispatcher
import com.grappim.hateitorrateit.utils.DateTimeUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiModelsMapper @Inject constructor(
    private val dateTimeUtils: DateTimeUtils,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun toDocumentUi(doc: Document): DocumentListUI = withContext(ioDispatcher) {
        val formattedCreatedDate = dateTimeUtils
            .formatToDemonstrate(doc.createdDate, true)
        DocumentListUI(
            id = doc.id.toString(),
            name = doc.name,
            createdDate = formattedCreatedDate,
            preview = doc.filesUri.firstOrNull()?.uriString ?: "",
            documentFolderName = doc.documentFolderName,
            shop = doc.shop,
            type = doc.type,
        )
    }

    suspend fun toDocumentDetailsUi(doc: Document): DocumentDetailsUi = withContext(ioDispatcher) {
        val formattedCreatedDate = dateTimeUtils
            .formatToDemonstrate(doc.createdDate, true)
        DocumentDetailsUi(
            id = doc.id.toString(),
            name = doc.name,
            createdDate = formattedCreatedDate,
            filesUri = doc.filesUri,
            documentFolderName = doc.documentFolderName,
            description = doc.description,
            shop = doc.shop,
            type = doc.type,
        )
    }

    suspend fun toDocumentDetailsImageU(doc: Document): DocumentDetailsImageUi =
        withContext(ioDispatcher) {
            DocumentDetailsImageUi(
                filesUri = doc.filesUri,
            )
        }
}
