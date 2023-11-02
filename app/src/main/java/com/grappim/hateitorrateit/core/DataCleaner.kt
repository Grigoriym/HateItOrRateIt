package com.grappim.hateitorrateit.core

import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.core.di.IoDispatcher
import com.grappim.hateitorrateit.data.ProductsRepository
import com.grappim.hateitorrateit.data.db.HateItOrRateItDatabase
import com.grappim.hateitorrateit.utils.DraftProduct
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
    private val productsRepository: ProductsRepository,
    private val hateItOrRateItDatabase: HateItOrRateItDatabase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun clearProductImage(
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

    suspend fun deleteProductFileData(
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

    suspend fun clearProductData(
        id: Long,
        productFolderName: String,
    ) = withContext(ioDispatcher) {
        Timber.d("start cleaning")
        fileUtils.deleteFolder(productFolderName)
        productsRepository.removeProductById(id)
    }

    suspend fun clearProductData(
        draftProduct: DraftProduct
    ) = withContext(ioDispatcher) {
        Timber.d("start cleaning")
        fileUtils.deleteFolder(draftProduct.folderName)
        productsRepository.removeProductById(draftProduct.id)
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