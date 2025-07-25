package com.grappim.hateitorrateit.data.cleanerimpl

import com.grappim.hateitorrateit.core.async.IoDispatcher
import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.cleanerapi.EmptyFilesCleaner
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.CancellationException
import javax.inject.Inject

/**
 * This class cleans any empty/non-created files. e.g. when we started creating a product,
 * but something happened and the app was closed
 */
@Suppress("TooGenericExceptionCaught")
class EmptyFilesCleanerImpl @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val dataCleaner: DataCleaner,
    private val productsRepository: ProductsRepository
) : EmptyFilesCleaner {
    override suspend fun clean() = withContext(dispatcher) {
        try {
            val emptyFiles = productsRepository.getEmptyFiles()
            emptyFiles.forEach { file ->
                Timber.d("Cleaning unused data: $file")
                dataCleaner.deleteProductData(
                    productId = file.id,
                    productFolderName = file.productFolderName
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}
