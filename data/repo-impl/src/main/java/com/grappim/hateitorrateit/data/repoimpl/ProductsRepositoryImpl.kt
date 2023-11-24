package com.grappim.hateitorrateit.data.repoimpl

import androidx.sqlite.db.SimpleSQLiteQuery
import com.grappim.hateitorrateit.commons.IoDispatcher
import com.grappim.hateitorrateit.data.db.dao.ProductsDao
import com.grappim.hateitorrateit.data.db.entities.ProductEntity
import com.grappim.hateitorrateit.data.db.wrapWithPercentWildcards
import com.grappim.hateitorrateit.data.db.wrapWithSingleQuotes
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.domain.CreateProduct
import com.grappim.hateitorrateit.domain.DraftProduct
import com.grappim.hateitorrateit.domain.EmptyFileData
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.domain.Product
import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.utils.DateTimeUtils
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
class ProductsRepositoryImpl @Inject constructor(
    private val dateTimeUtils: DateTimeUtils,
    private val productsDao: ProductsDao,
    private val localDataStorage: LocalDataStorage,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ProductsRepository {

    override suspend fun getProductById(id: Long): Product {
        val entity = productsDao.getProductById(id)
        val domain = entity.productEntity.toProduct(entity.files)
        return domain
    }

    override suspend fun updateProduct(
        id: Long,
        name: String,
        description: String,
        shop: String,
        type: HateRateType,
    ) = withContext(ioDispatcher) {
        productsDao.updateProduct(id, name, description, shop, type)
    }

    override suspend fun updateImagesInProduct(
        id: Long,
        files: List<ProductImageData>
    ) = withContext(ioDispatcher) {
        val filesEntity = files.toEntities(id)
        productsDao.insertImages(filesEntity)
    }

    override suspend fun addDraftProduct(): DraftProduct = withContext(ioDispatcher) {
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

    override suspend fun getEmptyFiles(): List<EmptyFileData> =
        productsDao.getEmptyFiles().toEmptyFilesData()

    override suspend fun deleteEmptyFiles() = productsDao.deleteEmptyFiles()

    override suspend fun deleteProductImage(id: Long, name: String) =
        productsDao.deleteProductImageByIdAndName(id, name)

    override fun getProductsFlow(
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

    override suspend fun removeProductById(id: Long) {
        productsDao.deleteProductAndImagesById(id)
    }

    override suspend fun addProduct(product: CreateProduct) = withContext(ioDispatcher) {
        val entity = product.toEntity()
        val list = product.toImageDataEntityList()
        productsDao.updateProductAndImages(
            productEntity = entity,
            list = list
        )
    }
}
