package com.grappim.hateitorrateit.data

import androidx.sqlite.db.SimpleSQLiteQuery
import com.grappim.domain.HateRateType
import com.grappim.domain.Product
import com.grappim.domain.ProductImageData
import com.grappim.hateitorrateit.core.di.IoDispatcher
import com.grappim.hateitorrateit.data.db.ProductsDao
import com.grappim.hateitorrateit.data.db.entities.ProductEntity
import com.grappim.hateitorrateit.data.db.wrapWithPercentWildcards
import com.grappim.hateitorrateit.data.db.wrapWithSingleQuotes
import com.grappim.hateitorrateit.data.mappers.toEntities
import com.grappim.hateitorrateit.data.mappers.toEntity
import com.grappim.hateitorrateit.data.mappers.toImageDataEntityList
import com.grappim.hateitorrateit.data.mappers.toProduct
import com.grappim.hateitorrateit.data.storage.local.LocalDataStorage
import com.grappim.hateitorrateit.model.CreateProduct
import com.grappim.hateitorrateit.utils.DateTimeUtils
import com.grappim.hateitorrateit.utils.DraftProduct
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsRepository @Inject constructor(
    private val dateTimeUtils: DateTimeUtils,
    private val productsDao: ProductsDao,
    private val localDataStorage: LocalDataStorage,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun getProductById(id: Long): Product {
        val entity = productsDao.getProductById(id)
        val domain = entity.productEntity.toProduct(entity.files)
        return domain
    }

    suspend fun updateProduct(
        id: Long,
        name: String,
        description: String,
        shop: String,
        type: HateRateType,
    ) = withContext(ioDispatcher) {
        productsDao.updateProduct(id, name, description, shop, type)
    }

    suspend fun updateImagesInProduct(
        id: Long,
        files: List<ProductImageData>
    ) = withContext(ioDispatcher) {
        val filesEntity = files.toEntities(id)
        productsDao.insertImages(filesEntity)
    }

    suspend fun addDraftProduct(): DraftProduct = withContext(ioDispatcher) {
        val nowDate = dateTimeUtils.getDateTimeUTCNow()
        val type = localDataStorage.typeFlow.first()
        val folderDate = dateTimeUtils.formatToGDrive(nowDate)
        val productEntity = ProductEntity(
            name = "",
            createdDate = nowDate,
            productFolderName = "",
            description = "",
            shop = "",
            type = type,
        )

        val id = productsDao.insertProduct(productEntity)
        val folderName = "${id}_${folderDate}"
        productsDao.updateProductFolderName(folderName, id)
        DraftProduct(
            id = id,
            date = nowDate,
            folderName = folderName,
            type = type,
        )
    }

    suspend fun getEmptyFiles() = productsDao.getEmptyFiles()

    suspend fun deleteEmptyFiles() = productsDao.deleteEmptyFiles()

    suspend fun deleteProductImage(id: Long, name: String) =
        productsDao.deleteProductImageByIdAndName(id, name)

    fun getProductsFlow(
        query: String,
        type: HateRateType?,
    ): Flow<List<Product>> = flow {
        emitAll(if (query.isEmpty() && type == null) {
            productsDao.getAllProductsFlow()
        } else {
            val sqLiteQuery = buildSqlQuery(query, type)
            productsDao.getAllProductsByRawQueryFlow(SimpleSQLiteQuery(sqLiteQuery))
        }.mapLatest { list ->
            list.map { it.toProduct() }
        })
    }

    private fun buildSqlQuery(
        query: String,
        type: HateRateType?
    ): String {
        val sqlQuery = StringBuilder("SELECT * FROM products_table ")
        if (query.isNotEmpty() || type != null) {
            sqlQuery.append("WHERE ")
        }
        if (query.isNotEmpty()) {
            sqlQuery.append(
                "(name LIKE ${
                    query.wrapWithPercentWildcards().wrapWithSingleQuotes()
                } "
            )
            sqlQuery.append(
                "OR shop LIKE ${
                    query.wrapWithPercentWildcards().wrapWithSingleQuotes()
                } "
            )
            sqlQuery.append(
                "OR description LIKE ${
                    query.wrapWithPercentWildcards().wrapWithSingleQuotes()
                }) "
            )
        }
        if (type != null) {
            if (query.isNotEmpty()) {
                sqlQuery.append("AND ")
            }
            sqlQuery.append("type=${type.name.wrapWithSingleQuotes()} ")
        }
        sqlQuery.append("AND isCreated=1 ")
        sqlQuery.append("ORDER BY createdDate DESC")
        Timber.d("sqlQuery: $sqlQuery")
        return sqlQuery.toString()
    }

    suspend fun removeProductById(id: Long) {
        productsDao.deleteProductAndImagesById(id)
    }

    suspend fun addProduct(product: CreateProduct) = withContext(ioDispatcher) {
        val entity = product.toEntity()
        val list = product.toImageDataEntityList()
        productsDao.updateProductAndImages(
            productEntity = entity,
            list = list
        )
    }
}
