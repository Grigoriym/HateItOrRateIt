package com.grappim.hateitorrateit.data.cleanerimpl

import com.grappim.hateitorrateit.commons.IoDispatcher
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.db.dao.DatabaseDao
import com.grappim.hateitorrateit.data.db.wrapper.DatabaseWrapper
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.utils.file.deletion.FileDeletionUtils
import com.grappim.hateitorrateit.utils.file.pathmanager.FolderPathManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Besides removing data from DB, we also need to remove folder and files
 * from the app storage
 */
class DataCleanerImpl @Inject constructor(
    private val fileDeletionUtils: FileDeletionUtils,
    private val productsRepository: ProductsRepository,
    private val databaseDao: DatabaseDao,
    private val databaseWrapper: DatabaseWrapper,
    private val folderPathManager: FolderPathManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DataCleaner {

    override suspend fun clearProductImage(
        productId: Long,
        imageName: String,
        uriString: String
    ): Boolean = withContext(ioDispatcher) {
        if (fileDeletionUtils.deleteFile(uriString)) {
            productsRepository.deleteProductImage(productId = productId, imageName = imageName)
            return@withContext true
        }
        false
    }

    override suspend fun deleteProductFileData(productId: Long, list: List<ProductImageData>) =
        withContext(ioDispatcher) {
            list.forEach {
                clearProductImage(
                    productId = productId,
                    imageName = it.name,
                    uriString = it.uriString
                )
            }
        }

    override suspend fun clearProductData(productId: Long, productFolderName: String) =
        withContext(ioDispatcher) {
            Timber.d("start cleaning")
            fileDeletionUtils.deleteFolder(productFolderName)
            productsRepository.deleteProductById(productId)
        }

    override suspend fun deleteTempFolder(productFolderName: String) = withContext(ioDispatcher) {
        Timber.d("start cleaning temp $productFolderName")
        fileDeletionUtils.deleteFolder(folderPathManager.getTempFolderName(productFolderName))
    }

    override suspend fun deleteBackupFolder(productFolderName: String) {
        fileDeletionUtils.deleteFolder(folderPathManager.getBackupFolderName(productFolderName))
    }

    override suspend fun clearAllData() {
        clearDatabaseData()
        clearFileSystemData()
    }

    private suspend fun clearDatabaseData() {
        databaseWrapper.clearAllTables()
        databaseDao.clearPrimaryKeyIndex()
    }

    private suspend fun clearFileSystemData() = fileDeletionUtils.clearMainFolder()
}
