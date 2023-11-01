package com.grappim.hateitorrateit.core

import com.grappim.domain.ProductFileData
import com.grappim.hateitorrateit.core.di.IoDispatcher
import com.grappim.hateitorrateit.data.DocsRepository
import com.grappim.hateitorrateit.data.db.HateItOrRateItDatabase
import com.grappim.hateitorrateit.utils.DraftDocument
import com.grappim.hateitorrateit.utils.FileUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Besides removing data from DB, we also need to remove folder and files
 * from the app storage
 */
class DataCleaner @Inject constructor(
    private val fileUtils: FileUtils,
    private val documentRepository: DocsRepository,
    private val hateItOrRateItDatabase: HateItOrRateItDatabase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun clearProductImage(
        id: Long,
        imageName: String,
        uriString: String,
    ): Boolean = withContext(ioDispatcher) {
        if (fileUtils.deleteFile(uriString)) {
            documentRepository.deleteDocumentImage(id, imageName)
            return@withContext true
        }
        false
    }

    suspend fun deleteProductFileData(
        id: Long,
        list: List<ProductFileData>
    ) = withContext(ioDispatcher) {
        list.forEach {
            clearProductImage(
                id = id,
                imageName = it.name,
                uriString = it.uriString,
            )
        }
    }

    suspend fun clearDocumentData(
        id: Long,
        documentFolderName: String,
    ) = withContext(ioDispatcher) {
        Timber.d("start cleaning")
        fileUtils.deleteFolder(documentFolderName)
        documentRepository.removeDocumentById(id)
    }

    suspend fun clearDocumentData(
        draftDocument: DraftDocument
    ) = withContext(ioDispatcher) {
        Timber.d("start cleaning")
        fileUtils.deleteFolder(draftDocument.folderName)
        documentRepository.removeDocumentById(draftDocument.id)
    }

    suspend fun clearAllData() = withContext(ioDispatcher) {
        hateItOrRateItDatabase.runInTransaction {
            runBlocking {
                hateItOrRateItDatabase.clearAllTables()
                hateItOrRateItDatabase.databaseDao().clearPrimaryKeyIndex()
            }
        }

        fileUtils.getMainFolder("").deleteRecursively()
    }
}