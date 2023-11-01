package com.grappim.hateitorrateit.model

import com.grappim.domain.Product
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

    suspend fun toProductUi(product: Product): ProductListUI = withContext(ioDispatcher) {
        val formattedCreatedDate = dateTimeUtils
            .formatToDemonstrate(product.createdDate, true)
        ProductListUI(
            id = product.id.toString(),
            name = product.name,
            createdDate = formattedCreatedDate,
            previewUriString = product.filesUri.firstOrNull()?.uriString ?: "",
            productFolderName = product.productFolderName,
            shop = product.shop,
            type = product.type,
        )
    }

    suspend fun toProductDetailsUi(product: Product): ProductDetailsUi = withContext(ioDispatcher) {
        val formattedCreatedDate = dateTimeUtils
            .formatToDemonstrate(product.createdDate, true)
        ProductDetailsUi(
            id = product.id.toString(),
            name = product.name,
            createdDate = formattedCreatedDate,
            filesUri = product.filesUri,
            productFolderName = product.productFolderName,
            description = product.description,
            shop = product.shop,
            type = product.type,
        )
    }

    suspend fun toProductDetailsImageUI(product: Product): ProcuctDetailsImageUi =
        withContext(ioDispatcher) {
            ProcuctDetailsImageUi(
                filesUri = product.filesUri,
            )
        }
}
