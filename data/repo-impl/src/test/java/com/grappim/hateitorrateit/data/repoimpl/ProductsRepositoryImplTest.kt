package com.grappim.hateitorrateit.data.repoimpl

import app.cash.turbine.test
import com.grappim.hateitorrateit.data.db.dao.ProductsDao
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.DraftProduct
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.data.repoimpl.helpers.SqlQueryBuilder
import com.grappim.hateitorrateit.data.repoimpl.mappers.ProductMapper
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsRepositoryImplTest {

    private val dateTimeUtils: DateTimeUtils = mockk()

    private val productsDao: ProductsDao = mockk()
    private val localDataStorage: LocalDataStorage = mockk()
    private val productsMapper: ProductMapper = mockk()
    private val sqlQueryBuilder: SqlQueryBuilder = mockk()

    private val repository: ProductsRepository = ProductsRepositoryImpl(
        dateTimeUtils = dateTimeUtils,
        productsDao = productsDao,
        localDataStorage = localDataStorage,
        productsMapper = productsMapper,
        sqlQueryBuilder = sqlQueryBuilder,
        ioDispatcher = UnconfinedTestDispatcher()
    )

    @Test
    fun `getProductById should return Product`() = runTest {
        val product = getProduct()
        val productWithImagesEntity = getProductWithImagesEntity()

        coEvery { productsDao.getProductById(any()) } returns productWithImagesEntity
        coEvery { productsMapper.toProduct(any()) } returns product

        val actual = repository.getProductById(ID)

        coVerify { productsDao.getProductById(ID) }
        coVerify { productsMapper.toProduct(productWithImagesEntity) }

        assertEquals(product, actual)
    }

    @Test
    fun `updateProduct should update product`() = runTest {
        coEvery { productsDao.updateProduct(any(), any(), any(), any(), any()) } just Runs

        repository.updateProduct(
            id = ID,
            name = "name",
            description = "description",
            shop = "shop",
            type = HateRateType.HATE
        )

        coVerify { productsDao.updateProduct(ID, "name", "description", "shop", HateRateType.HATE) }
    }

    @Test
    fun `updateProduct with Product should update product`() = runTest {
        val product = getProduct()
        val entity = getProductEntity()

        coEvery { productsDao.updateProduct(productEntity = any()) } just Runs
        coEvery { productsMapper.toProductEntity(product = any()) } returns entity

        repository.updateProduct(
            product = product
        )

        coVerify { productsMapper.toProductEntity(product = product) }
        coVerify { productsDao.updateProduct(productEntity = entity) }
    }

    @Test
    fun `updateImagesInProduct should update images in product`() = runTest {
        val images = listOf(getProductImageDataEntity())
        val files = listOf(getProductImageData())

        coEvery { productsDao.upsertImages(any()) } just Runs
        coEvery {
            productsMapper.toProductImageDataEntityList(
                any(),
                any()
            )
        } returns images

        repository.updateImagesInProduct(
            id = ID,
            images = files
        )

        coVerify { productsMapper.toProductImageDataEntityList(ID, files) }
        coVerify { productsDao.upsertImages(images) }
    }

    @Test
    fun `addDraftProduct should add draft product`() = runTest {
        val nowDate = nowDate
        val type = HateRateType.HATE
        val folderDate = "2023-12-12_12-12-12"
        val folderName = "${ID}_$folderDate"
        val draftProduct = DraftProduct(
            id = ID,
            date = nowDate,
            productFolderName = folderName,
            type = type
        )

        coEvery { dateTimeUtils.getDateTimeUTCNow() } returns nowDate
        coEvery { dateTimeUtils.formatToDocumentFolder(any()) } returns folderDate
        coEvery { productsDao.insert(any()) } returns ID
        coEvery { productsDao.updateProductFolderName(any(), any()) } just Runs
        coEvery { localDataStorage.typeFlow } returns flowOf(type)

        val actual = repository.addDraftProduct()

        coVerify { dateTimeUtils.getDateTimeUTCNow() }
        coVerify { dateTimeUtils.formatToDocumentFolder(nowDate) }
        coVerify { productsDao.insert(any()) }
        coVerify { productsDao.updateProductFolderName(folderName, ID) }

        assertEquals(draftProduct, actual)
    }

    @Test
    fun `getEmptyFiles should return empty files`() = runTest {
        val emptyFiles = getProductWithImagesEntityList()
        val emptyFileData = getListOfEmptyFileData()
        coEvery { productsDao.getEmptyFiles() } returns emptyFiles
        coEvery { productsMapper.toEmptyFileList(any()) } returns emptyFileData

        val actual = repository.getEmptyFiles()

        coVerify { productsDao.getEmptyFiles() }
        coVerify { productsMapper.toEmptyFileList(emptyFiles) }

        assertEquals(emptyFileData, actual)
    }

    @Test
    fun `deleteEmptyFiles should delete empty files`() = runTest {
        coEvery { productsDao.deleteEmptyFiles() } just Runs

        repository.deleteEmptyFiles()

        coVerify { productsDao.deleteEmptyFiles() }
    }

    @Test
    fun `deleteProductImage should delete product image`() = runTest {
        coEvery { productsDao.deleteProductImageByIdAndName(any(), any()) } just Runs

        repository.deleteProductImage(productId = ID, imageName = "name")

        coVerify { productsDao.deleteProductImageByIdAndName(ID, "name") }
    }

    @Test
    fun `removeProductById should remove product by id`() = runTest {
        coEvery { productsDao.deleteProductAndImagesById(any()) } just Runs

        repository.deleteProductById(ID)

        coVerify { productsDao.deleteProductAndImagesById(ID) }
    }

    @Test
    fun `addProduct should add product`() = runTest {
        val createProduct = getCreateProduct()
        val productEntity = getProductEntity()
        val images = getProductImageDataEntityList()

        coEvery { productsMapper.toProductEntity(createProduct = any()) } returns productEntity
        coEvery { productsMapper.toProductImageDataEntityList(any()) } returns images
        coEvery { productsDao.updateProductAndImages(any(), any()) } just Runs

        repository.addProduct(createProduct)

        coVerify { productsMapper.toProductEntity(createProduct) }
        coVerify { productsMapper.toProductImageDataEntityList(createProduct) }
        coVerify { productsDao.updateProductAndImages(productEntity, images) }
    }

    @Test
    fun `getProductsFlow with query should return products flow by query`() = runTest {
        val query = "query"
        val type = HateRateType.RATE

        val productsWithImages = getProductWithImagesEntityList()
        val sqlQuery = "sqlQuery"
        val product = getProduct()

        coEvery { sqlQueryBuilder.buildSqlQuery(any(), any()) } returns sqlQuery
        coEvery { productsDao.getAllProductsByRawQueryFlow(any()) } returns flowOf(
            productsWithImages
        )
        coEvery { productsMapper.toProduct(any(), any()) } returns product

        repository.getProductsFlow(query, type).test {
            assertEquals(listOf(product), awaitItem())

            coVerify { sqlQueryBuilder.buildSqlQuery(query, type) }
            coVerify(exactly = 0) { productsDao.getAllProductsFlow() }
            awaitComplete()
        }
    }

    @Test
    fun `getProductsFlow with empty query should return products flow by query`() = runTest {
        val query = ""
        val type = HateRateType.RATE

        val productsWithImages = getProductWithImagesEntityList()
        val sqlQuery = "sqlQuery"
        val product = getProduct()

        coEvery { sqlQueryBuilder.buildSqlQuery(any(), any()) } returns sqlQuery
        coEvery { productsDao.getAllProductsByRawQueryFlow(any()) } returns flowOf(
            productsWithImages
        )
        coEvery { productsMapper.toProduct(any(), any()) } returns product

        repository.getProductsFlow(query, type).test {
            assertEquals(listOf(product), awaitItem())

            coVerify { sqlQueryBuilder.buildSqlQuery(query, type) }
            coVerify(exactly = 0) { productsDao.getAllProductsFlow() }
            awaitComplete()
        }
    }

    @Test
    fun `getProductsFlow with query and null type should return products flow by query`() = runTest {
        val query = "query"
        val type: HateRateType? = null

        val productsWithImages = getProductWithImagesEntityList()
        val sqlQuery = "sqlQuery"
        val product = getProduct()

        coEvery { sqlQueryBuilder.buildSqlQuery(any(), any()) } returns sqlQuery
        coEvery { productsDao.getAllProductsByRawQueryFlow(any()) } returns flowOf(
            productsWithImages
        )
        coEvery { productsMapper.toProduct(any(), any()) } returns product

        repository.getProductsFlow(query, type).test {
            assertEquals(listOf(product), awaitItem())

            coVerify { sqlQueryBuilder.buildSqlQuery(query, type) }
            coVerify(exactly = 0) { productsDao.getAllProductsFlow() }
            awaitComplete()
        }
    }

    @Test
    fun `getProductsFlow with empty query and null type should return all products flow`() = runTest {
        val query = ""
        val type: HateRateType? = null

        val productWithImages = getProductWithImagesEntity()
        val product = getProduct()

        coEvery { productsDao.getAllProductsFlow() } returns flowOf(
            listOf(productWithImages)
        )
        coEvery { productsMapper.toProduct(any(), any()) } returns product

        repository.getProductsFlow(query, type).test {
            assertEquals(listOf(product), awaitItem())

            coVerify { productsDao.getAllProductsFlow() }
            coVerify(exactly = 0) { sqlQueryBuilder.buildSqlQuery(query, type) }
            coVerify(exactly = 0) { productsDao.getAllProductsByRawQueryFlow(any()) }
            coVerify {
                productsMapper.toProduct(
                    productEntity = productWithImages.productEntity,
                    images = productWithImages.files
                )
            }
            awaitComplete()
        }
    }

    @Test
    fun `updateProductWithImages should update the data in dao`() = runTest {
        val product = getProduct()
        val images = getProductImageDataList()
        val productEntity = getProductEntity()
        val entityImages = getProductImageDataEntityList()

        coEvery { productsMapper.toProductEntity(product = any()) } returns productEntity
        coEvery {
            productsMapper.toProductImageDataEntityList(
                productId = any(),
                images = any()
            )
        } returns entityImages
        coEvery {
            productsDao.updateProductAndImages(
                productEntity = any(),
                images = any()
            )
        } just Runs

        repository.updateProductWithImages(product, images)

        coVerify { productsMapper.toProductEntity(product) }
        coVerify {
            productsMapper.toProductImageDataEntityList(
                productId = product.id,
                images = images
            )
        }
        coVerify {
            productsDao.updateProductAndImages(
                productEntity = productEntity,
                images = entityImages
            )
        }
    }
}
