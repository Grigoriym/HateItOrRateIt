package com.grappim.hateitorrateit.core

import com.grappim.hateitorrateit.core.di.ApplicationCoroutineScope
import com.grappim.hateitorrateit.data.DocsRepository
import com.grappim.hateitorrateit.utils.DraftDocument
import com.grappim.hateitorrateit.utils.FileUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Besides removing data from DB, we also need to remove folder and files
 * from the app storage
 */
class DataCleaner @Inject constructor(
    private val fileUtils: FileUtils,
    private val documentRepository: DocsRepository,
    @ApplicationCoroutineScope private val appDispatcher: CoroutineScope
) {

    suspend fun clearDocumentData(
        id: Long,
        documentFolderName: String,
    ) = appDispatcher.launch {
        Timber.d("start cleaning")
        fileUtils.deleteFolder(documentFolderName)
        documentRepository.removeDocumentById(id)
    }.join()

    suspend fun clearDocumentData(
        draftDocument: DraftDocument
    ) = appDispatcher.launch {
        Timber.d("start cleaning")
        fileUtils.deleteFolder(draftDocument.folderName)
        documentRepository.removeDocumentById(draftDocument.id)
    }.join()
}