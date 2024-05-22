package com.grappim.hateitorrateit.feature.details.ui

import com.grappim.hateitorrateit.commons.IoDispatcher
import com.grappim.hateitorrateit.data.repoapi.models.Product
import com.grappim.hateitorrateit.uikit.models.ProductDetailsImageUi
import com.grappim.hateitorrateit.uikit.models.ProductDetailsUi
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiModelsMapper @Inject constructor(
    private val dateTimeUtils: DateTimeUtils,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
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
