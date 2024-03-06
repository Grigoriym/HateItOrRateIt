package com.grappim.hateitorrateit.data.db

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.grappim.hateitorrateit.data.db.converters.DateTimeConverter
import com.grappim.hateitorrateit.data.db.dao.ProductsDao
import com.grappim.hateitorrateit.data.db.entities.PRODUCTS_TABLE
import com.grappim.hateitorrateit.data.db.entities.ProductEntity
import com.grappim.hateitorrateit.data.db.wrapper.DatabaseWrapper
import com.grappim.hateitorrateit.data.db.wrapper.DatabaseWrapperImpl
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.utils.datetime.DateTimeUtils
import com.grappim.hateitorrateit.utils.di.DateTimeModule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProductsDaoTest {

    private lateinit var productsDao: ProductsDao
    private lateinit var dbWrapper: DatabaseWrapper

    private val dateTimeUtils = mockk<DateTimeUtils>(relaxed = true)

    private val dateTimeFormatter = DateTimeModule.provideDtfToStore()

    @Before
    fun setup() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(
            context,
            HateItOrRateItDatabase::class.java
        ).addTypeConverter(
            DateTimeConverter(dateTimeUtils)
        ).build()

        dbWrapper = DatabaseWrapperImpl(
            db = db,
            ioDispatcher = UnconfinedTestDispatcher()
        )

        productsDao = dbWrapper.productsDao

        assertTrue(getAllProducts().isEmpty())
    }

    @After
    fun close() {
        dbWrapper.close()
    }

    @Test
    fun on_insert_getProductById_returnsCorrectProduct() = runTest {
        val date = prepareDate()

        val (entity, id) = prepareEntityAndId(date)

        val actual = productsDao.getProductById(id).productEntity
        val expected = entity.copy(productId = id)
        assertEquals(expected, actual)
    }

    @Test
    fun on_insert_should_emit_correct_product() = runTest {
        val date = prepareDate()

        val (entity, id) = prepareEntityAndId(date)

        productsDao.getAllProductsFlow().test {
            val list = awaitItem().map {
                it.productEntity
            }
            assertTrue(list.size == 1)

            val item = list.first()

            assertEquals(item.productId, id)
            assertProductData(item, entity)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllProductsFlow_should_not_return_emptyFiles() = runTest {
        val date = prepareDate()

        val nonEmptyFile = createProductEntity().copy(createdDate = date, isCreated = true)
        val emptyFile = createProductEntity().copy(createdDate = date, isCreated = false)

        productsDao.insert(nonEmptyFile)
        productsDao.insert(emptyFile)

        productsDao.getAllProductsFlow().test {
            val actual = awaitItem().map { it.productEntity }

            assertTrue(actual.size == 1)
            assertTrue(actual.first().isCreated)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun on_insert_and_delete_db_should_be_empty() = runTest {
        val date = prepareDate()

        val (_, id) = prepareEntityAndId(date)

        productsDao.getAllProductsFlow().test {
            assertTrue(awaitItem().isNotEmpty())

            productsDao.deleteById(id)
            assertTrue(awaitItem().isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insert_and_update_product_getProductById_should_return_updated_product() = runTest {
        val date = prepareDate()

        val (entity, id) = prepareEntityAndId(date)

        val newProduct = entity.copy(
            name = "name_2",
            description = "description_2",
            shop = "shop_2",
            type = HateRateType.RATE
        )
        productsDao.updateProduct(
            id = id,
            name = newProduct.name,
            description = newProduct.description,
            shop = newProduct.shop,
            type = newProduct.type
        )

        val actual = productsDao.getProductById(id).productEntity

        assertProductData(actual, newProduct)
    }

    @Test
    fun insert_and_update_product_withValues_getProductById_should_return_updated_product() =
        runTest {
            val date = prepareDate()

            val (entity, id) = prepareEntityAndId(date)

            val newDate = prepareDate()
            val newProduct = entity.copy(
                productId = id,
                name = "name_2",
                createdDate = newDate,
                productFolderName = "folder_2",
                description = "description_2",
                shop = "shop_2",
                type = HateRateType.RATE,
                isCreated = true
            )
            productsDao.updateProduct(newProduct)

            val actual = productsDao.getProductById(id).productEntity

            assertProductData(actual, newProduct)
        }

    @Test
    fun on_insert_getAllProductsFlow_emitsCorrectProducts() = runTest {
        val date = prepareDate()

        val productEntity1 = createProductEntity().copy(createdDate = date)
        val productEntity2 = createProductEntity().copy(
            createdDate = date,
            name = "name_2",
            productFolderName = "product_2",
            description = "description_2",
            shop = "shop_2"
        )
        val productEntity3 = createProductEntity().copy(
            createdDate = date,
            name = "name_3",
            productFolderName = "product_3",
            description = "description_3",
            shop = "shop_3"
        )

        productsDao.insert(productEntity1)
        productsDao.insert(productEntity2)
        productsDao.insert(productEntity3)

        productsDao.getAllProductsFlow().test {
            val emittedProducts = awaitItem().map {
                it.productEntity
            }

            assertEquals(3, emittedProducts.size)

            assertContainsProduct(emittedProducts, productEntity1)
            assertContainsProduct(emittedProducts, productEntity2)
            assertContainsProduct(emittedProducts, productEntity3)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateProductAndImages_should_correctly_update_data() = runTest {
        val date = prepareDate()

        val (entity, id) = prepareEntityAndId(date)
        val images = getProductImageList(id)

        productsDao.updateProductAndImages(entity, images)

        productsDao.getAllProductsFlow().test {
            val fullItem = awaitItem()

            assertTrue(fullItem.size == 1)

            val item = fullItem.first()
            assertProductData(item.productEntity, entity)

            val files = item.files
            assertNotNull(files)
            assertTrue(files.size == 2)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun on_insertingProductAndImages_on_deleteProductAndImagesById_should_delete_correctly() =
        runTest {
            val date = prepareDate()

            val entity = createProductEntity().copy(createdDate = date)
            val id = productsDao.insert(entity)
            val images = getProductImageList(id)
            productsDao.upsertImages(images)

            productsDao.getAllProductsFlow().test {
                val initialItem = awaitItem()
                assertTrue(initialItem.size == 1)
                assertTrue(initialItem.first().files?.size == 2)

                productsDao.deleteProductAndImagesById(id)

                assertTrue(awaitItem().isEmpty())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun on_updateProductFolderName_should_correctly_update() = runTest {
        val newFolderName = "new_folder_name"

        val date = prepareDate()
        val entity = createProductEntity().copy(createdDate = date)
        val id = productsDao.insert(entity)

        assertNotNull(productsDao.getProductById(id))

        productsDao.updateProductFolderName(newFolderName, id)

        val actual = productsDao.getProductById(id).productEntity.productFolderName

        assertEquals(actual, newFolderName)
    }

    @Test
    fun on_getEmptyFiles_should_return_only_emptyFiles() = runTest {
        val date = prepareDate()
        val nonEmptyFile = createProductEntity().copy(createdDate = date, isCreated = true)
        val emptyFile = createProductEntity().copy(createdDate = date, isCreated = false)

        productsDao.insert(nonEmptyFile)
        productsDao.insert(emptyFile)

        val emptyFileList = productsDao.getEmptyFiles()

        assertTrue(emptyFileList.size == 1)
        assertFalse(emptyFileList.first().productEntity.isCreated)
    }

    @Test
    fun on_deleteEmptyFiles_should_delete_only_emptyFiles() = runTest {
        val date = prepareDate()
        val nonEmptyFile = createProductEntity().copy(createdDate = date, isCreated = true)
        val emptyFile = createProductEntity().copy(createdDate = date, isCreated = false)

        productsDao.insert(nonEmptyFile)
        productsDao.insert(emptyFile)

        val emptyFileList = productsDao.getEmptyFiles()
        assertTrue(emptyFileList.size == 1)
        assertFalse(emptyFileList.first().productEntity.isCreated)

        productsDao.deleteEmptyFiles()

        val emptyFileListAfterDeletion = productsDao.getEmptyFiles()
        assertTrue(emptyFileListAfterDeletion.isEmpty())
    }

    @Test
    fun on_getAllProductsByRawQueryFlow_should_return_correct_products() = runTest {
        val date = prepareDate()
        val nonEmptyFile = createProductEntity().copy(createdDate = date, isCreated = true)
        val emptyFile = createProductEntity().copy(createdDate = date, isCreated = false)

        productsDao.insert(nonEmptyFile)
        productsDao.insert(emptyFile)

        val sqLiteQuery = SimpleSQLiteQuery(
            "SELECT * FROM $PRODUCTS_TABLE WHERE " +
                "isCreated=1"
        )

        productsDao.getAllProductsByRawQueryFlow(sqLiteQuery).test {
            val actual = awaitItem()

            assertTrue(actual.size == 1)
            assertTrue(actual.first().productEntity.isCreated)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun on_deleteImagesByProductId_should_delete_only_images_from_product() = runTest {
        val date = prepareDate()

        val (_, id) = prepareEntityAndId(date)
        val images = getProductImageList(id)
        productsDao.upsertImages(images)

        productsDao.getAllProductsFlow().test {
            val initial = awaitItem()
            assertTrue(initial.size == 1)
            assertTrue(initial.first().files?.size == 2)

            productsDao.deleteImagesByProductId(id)

            val actual = awaitItem()
            assertTrue(actual.size == 1)
            assertTrue(actual.first().files.isNullOrEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun on_deleteProductImageByIdAndName_should_delete_correct_image() = runTest {
        val date = prepareDate()

        val (_, id) = prepareEntityAndId(date)
        val images = getProductImageList(id)
        productsDao.upsertImages(images)

        productsDao.getAllProductsFlow().test {
            val initial = awaitItem()
            assertTrue(initial.size == 1)
            assertTrue(initial.first().files?.size == 2)

            productsDao.deleteProductImageByIdAndName(id, PRODUCT_IMAGE_NAME)

            val actual = awaitItem()
            assertTrue(actual.size == 1)
            assertTrue(actual.first().files?.size == 1)
            assertEquals(actual.first().files?.first()?.name, PRODUCT_IMAGE_NAME_2)

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun assertProductData(actual: ProductEntity, expected: ProductEntity) {
        assertEquals(actual.name, expected.name)
        assertEquals(actual.description, expected.description)
        assertEquals(actual.shop, expected.shop)
        assertEquals(actual.type, expected.type)
        assertEquals(actual.createdDate, expected.createdDate)
        assertEquals(actual.isCreated, expected.isCreated)
        assertEquals(actual.productFolderName, expected.productFolderName)
    }

    private suspend fun prepareEntityAndId(date: OffsetDateTime): Pair<ProductEntity, Long> {
        val entity = createProductEntity().copy(createdDate = date)
        val id = productsDao.insert(entity)
        return Pair(entity, id)
    }

    private fun prepareDate(): OffsetDateTime {
        val date = getNowDate()
        setupFormatter(date)
        return date
    }

    private fun assertContainsProduct(
        emittedProducts: List<ProductEntity>,
        originalProduct: ProductEntity
    ) {
        assertTrue(
            emittedProducts.any {
                it.name == originalProduct.name &&
                    it.createdDate == originalProduct.createdDate &&
                    it.productFolderName == originalProduct.productFolderName &&
                    it.description == originalProduct.description &&
                    it.shop == originalProduct.shop &&
                    it.type == originalProduct.type
            }
        )
    }

    private suspend fun getAllProducts(): List<ProductEntity> =
        productsDao.getAllProductsFlow().firstOrNull()?.map {
            it.productEntity
        }.orEmpty()

    private fun setupFormatter(date: OffsetDateTime) {
        every { dateTimeUtils.formatToStoreInDb(any()) } returns dateTimeFormatter.format(date)
        every { dateTimeUtils.parseFromStoringInDb(any()) } returns date
    }
}
