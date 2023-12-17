package com.grappim.hateitorrateit.data.repoimpl

import androidx.sqlite.db.SimpleSQLiteQuery
import com.grappim.hateitorrateit.commons.IoDispatcher
import com.grappim.hateitorrateit.data.db.dao.ProductsDao
import com.grappim.hateitorrateit.data.db.entities.ProductEntity
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsRepositoryImpl @Inject constructor(
    private val dateTimeUtils: DateTimeUtils,
    private val productsDao: ProductsDao,
    private val localDataStorage: LocalDataStorage,
    private val productsMapper: ProductMapper,
    private val sqlQueryBuilder: SqlQueryBuilder,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ProductsRepository {

    override suspend fun getProductById(id: Long): Product {
        val entity = productsDao.getProductById(id)
        return productsMapper.toProduct(entity)
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
        val filesEntity = productsMapper.toProductImageDataEntityList(id, files)
        productsDao.insertImages(filesEntity)
    }

    override suspend fun addDraftProduct(): DraftProduct = withContext(ioDispatcher) {
        val nowDate = dateTimeUtils.getDateTimeUTCNow()
        val type = localDataStorage.typeFlow.first()
        val folderDate = dateTimeUtils.formatToDocumentFolder(nowDate)
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
        productsMapper.toEmptyFileDataList(productsDao.getEmptyFiles())

    override suspend fun deleteEmptyFiles() = productsDao.deleteEmptyFiles()

    override suspend fun deleteProductImage(
        id: Long,
        name: String,
    ) = productsDao.deleteProductImageByIdAndName(id, name)

    override suspend fun removeProductById(id: Long) {
        productsDao.deleteProductAndImagesById(id)
    }

    override suspend fun addProduct(product: CreateProduct) = withContext(ioDispatcher) {
        val productEntity = productsMapper.toProductEntity(product)
        val images = productsMapper.toProductImageDataEntityList(product)
        productsDao.updateProductAndImages(
            productEntity = productEntity,
            images = images
        )
    }

    override fun getProductsFlow(
        query: String,
        type: HateRateType?,
    ): Flow<List<Product>> = flow {
        emitAll(if (query.isEmpty() && type == null) {
            productsDao.getAllProductsFlow()
        } else {
            val sqLiteQuery = sqlQueryBuilder.buildSqlQuery(query, type)
            productsDao.getAllProductsByRawQueryFlow(SimpleSQLiteQuery(sqLiteQuery))
        }.mapLatest { list ->
            list.map {
                productsMapper.toProduct(
                    productEntity = it.productEntity,
                    files = it.files
                )
            }
        })
    }
}
