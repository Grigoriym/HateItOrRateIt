package com.grappim.hateitorrateit.feature.home.ui.utils

import com.grappim.hateitorrateit.commons.IoDispatcher
import com.grappim.hateitorrateit.data.repoapi.models.Product
import com.grappim.hateitorrateit.feature.home.ui.models.ProductListUI
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeUIModelsMapperImpl @Inject constructor(
    private val dateTimeUtils: DateTimeUtils,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : HomeUIModelsMapper {
    override suspend fun toProductUi(product: Product): ProductListUI = withContext(ioDispatcher) {
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
}
