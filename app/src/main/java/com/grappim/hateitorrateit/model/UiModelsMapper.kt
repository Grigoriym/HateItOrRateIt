package com.grappim.hateitorrateit.model

import com.grappim.hateitorrateit.domain.Document
import com.grappim.hateitorrateit.utils.DateTimeUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiModelsMapper @Inject constructor(
    private val dateTimeUtils: DateTimeUtils,
) {

    fun toDocumentUi(doc: Document): DocumentListUI {
        val formattedCreatedDate = dateTimeUtils
            .formatToDemonstrate(doc.createdDate, true)

        return DocumentListUI(
            id = doc.id.toString(),
            name = doc.name,
            createdDate = formattedCreatedDate,
            preview = doc
                .filesUri
                .firstOrNull()
                ?.uriString ?: "",
            documentFolderName = doc.documentFolderName,
            shop = doc.shop,
        )
    }

    fun toDocumentDetailsUi(doc: Document): DocumentDetailsUi {
        val formattedCreatedDate = dateTimeUtils
            .formatToDemonstrate(doc.createdDate, true)

        return DocumentDetailsUi(
            id = doc.id.toString(),
            name = doc.name,
            createdDate = formattedCreatedDate,
            filesUri = doc.filesUri,
            documentFolderName = doc.documentFolderName,
            description = doc.description,
            shop = doc.shop,
        )
    }
}
