package com.grappim.hateitorrateit.data.cleanerimpl

import com.grappim.hateitorrateit.commons.IoDispatcher
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.db.HateItOrRateItDatabase
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.domain.DraftProduct
import com.grappim.hateitorrateit.domain.ProductImageData
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
class DataCleanerImpl @Inject constructor(
    private val fileUtils: FileUtils,
    private val productsRepository: ProductsRepository,
    private val hateItOrRateItDatabase: HateItOrRateItDatabase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : DataCleaner {

    override suspend fun clearProductImage(
        id: Long,
        imageName: String,
        uriString: String,
    ): Boolean = withContext(ioDispatcher) {
        if (fileUtils.deleteFile(uriString)) {
            productsRepository.deleteProductImage(id, imageName)
            return@withContext true
        }
        false
    }

    override suspend fun deleteProductFileData(
        id: Long,
        list: List<ProductImageData>
    ) = withContext(ioDispatcher) {
        list.forEach {
            clearProductImage(
                id = id,
                imageName = it.name,
                uriString = it.uriString,
            )
        }
    }

    override suspend fun clearProductData(
        id: Long,
        productFolderName: String,
    ) = withContext(ioDispatcher) {
        Timber.d("start cleaning")
        fileUtils.deleteFolder(productFolderName)
        productsRepository.removeProductById(id)
    }

    override suspend fun clearProductData(
        draftProduct: DraftProduct
    ) = withContext(ioDispatcher) {
        Timber.d("start cleaning")
        fileUtils.deleteFolder(draftProduct.folderName)
        productsRepository.removeProductById(draftProduct.id)
    }

    override suspend fun clearAllData(): Boolean = withContext(ioDispatcher) {
        hateItOrRateItDatabase.runInTransaction {
            runBlocking {
                hateItOrRateItDatabase.clearAllTables()
                hateItOrRateItDatabase.databaseDao().clearPrimaryKeyIndex()
            }
        }

        fileUtils.getMainFolder("").deleteRecursively()
    }
}
