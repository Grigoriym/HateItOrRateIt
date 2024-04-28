package com.grappim.hateitorrateit.model

import com.grappim.hateitorrateit.commons.IoDispatcher
import com.grappim.hateitorrateit.datetime.DateTimeUtils
import com.grappim.hateitorrateit.domain.Product
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiModelsMapper @Inject constructor(
    private val dateTimeUtils: DateTimeUtils,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun toProductUi(product: Product): ProductListUI = withContext(ioDispatcher) {
        val formattedCreatedDate = dateTimeUtils
            .formatToDemonstrate(product.createdDate)
        ProductListUI(
            id = product.id.toString(),
            name = product.name,
            createdDate = formattedCreatedDate,
            previewUriString = product.images.firstOrNull()?.uriString ?: "",
            productFolderName = product.productFolderName,
            shop = product.shop,
            type = product.type
        )
    }

    suspend fun toProductDetailsUi(product: Product): ProductDetailsUi = withContext(ioDispatcher) {
        val formattedCreatedDate = dateTimeUtils
            .formatToDemonstrate(product.createdDate)
        ProductDetailsUi(
            id = product.id.toString(),
            name = product.name,
            createdDate = formattedCreatedDate,
            images = product.images,
            productFolderName = product.productFolderName,
            description = product.description,
            shop = product.shop,
            type = product.type
        )
    }

    suspend fun toProductDetailsImageUI(product: Product): ProductDetailsImageUi =
        withContext(ioDispatcher) {
            ProductDetailsImageUi(
                images = product.images
            )
        }
}
