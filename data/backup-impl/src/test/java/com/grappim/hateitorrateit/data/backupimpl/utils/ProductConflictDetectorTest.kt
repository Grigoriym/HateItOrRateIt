package com.grappim.hateitorrateit.data.backupimpl.utils

import com.grappim.hateitorrateit.data.backupapi.models.ProductExport
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.testing.domain.getFakeProduct
import com.grappim.hateitorrateit.testing.domain.getRandomLong
import com.grappim.hateitorrateit.testing.domain.getRandomString
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.time.OffsetDateTime

internal class ProductConflictDetectorTest {

    private val productsRepository = mockk<ProductsRepository>()
    private val sut = ProductConflictDetector(productsRepository)

    @Test
    fun `detectConflicts returns empty map when no existing products`() = runTest {
        val productsToImport = listOf(createProductExport())
        coEvery { productsRepository.getAllProducts() } returns persistentListOf()

        val result = sut.detectConflicts(productsToImport)

        Assert.assertTrue("Should return empty map when no existing products", result.isEmpty())
    }

    @Test
    fun `detectConflicts returns empty map when no matching products`() = runTest {
        val productsToImport = listOf(createProductExport())
        val existingProducts = persistentListOf(createProduct())
        coEvery { productsRepository.getAllProducts() } returns existingProducts

        val result = sut.detectConflicts(productsToImport)

        Assert.assertTrue("Should return empty map when no matching products", result.isEmpty())
    }

    @Test
    fun `detectConflicts finds exact match by name, type, shop, and description`() = runTest {
        val productName = getRandomString()
        val shop = getRandomString()
        val description = getRandomString()
        val type = HateRateType.HATE

        val productToImport = createProductExport(
            name = productName,
            shop = shop,
            description = description,
            type = type
        )
        val existingProduct = createProduct(
            name = productName,
            shop = shop,
            description = description,
            type = type
        )

        coEvery { productsRepository.getAllProducts() } returns persistentListOf(existingProduct)

        val result = sut.detectConflicts(listOf(productToImport))

        Assert.assertEquals("Should find one conflict", 1, result.size)
        Assert.assertEquals(
            "Should map import product to existing product",
            existingProduct,
            result[productToImport]
        )
    }

    @Test
    fun `detectConflicts is case insensitive for product names`() = runTest {
        val shop = getRandomString()
        val description = getRandomString()

        val productToImport = createProductExport(
            name = "Test Product",
            shop = shop,
            description = description
        )
        val existingProduct = createProduct(
            name = "test product",
            shop = shop,
            description = description
        )

        coEvery { productsRepository.getAllProducts() } returns persistentListOf(existingProduct)

        val result = sut.detectConflicts(listOf(productToImport))

        Assert.assertEquals("Should find conflict despite case difference", 1, result.size)
        Assert.assertEquals(
            "Should map import product to existing product",
            existingProduct,
            result[productToImport]
        )
    }

    @Test
    fun `detectConflicts does not match when type differs`() = runTest {
        val shop = getRandomString()
        val description = getRandomString()

        val productToImport = createProductExport(
            name = "Test Product",
            shop = shop,
            description = description,
            type = HateRateType.HATE
        )
        val existingProduct = createProduct(
            name = "Test Product",
            shop = shop,
            description = description,
            type = HateRateType.RATE
        )

        coEvery { productsRepository.getAllProducts() } returns persistentListOf(existingProduct)

        val result = sut.detectConflicts(listOf(productToImport))

        Assert.assertTrue("Should not match when types differ", result.isEmpty())
    }

    @Test
    fun `detectConflicts does not match when shop differs`() = runTest {
        val description = getRandomString()

        val productToImport = createProductExport(
            name = "Test Product",
            shop = "Shop A",
            description = description
        )
        val existingProduct = createProduct(
            name = "Test Product",
            shop = "Shop B",
            description = description
        )

        coEvery { productsRepository.getAllProducts() } returns persistentListOf(existingProduct)

        val result = sut.detectConflicts(listOf(productToImport))

        Assert.assertTrue("Should not match when shops differ", result.isEmpty())
    }

    @Test
    fun `detectConflicts does not match when description differs`() = runTest {
        val shop = getRandomString()

        val productToImport = createProductExport(
            name = "Test Product",
            shop = shop,
            description = "Description A"
        )
        val existingProduct = createProduct(
            name = "Test Product",
            shop = shop,
            description = "Description B"
        )

        coEvery { productsRepository.getAllProducts() } returns persistentListOf(existingProduct)

        val result = sut.detectConflicts(listOf(productToImport))

        Assert.assertTrue("Should not match when descriptions differ", result.isEmpty())
    }

    @Test
    fun `detectConflicts handles multiple products with mixed conflicts`() = runTest {
        val commonShop = getRandomString()
        val commonDescription = getRandomString()

        val conflictingProduct = createProductExport(
            name = "Matching Product",
            shop = commonShop,
            description = commonDescription
        )
        val nonConflictingProduct = createProductExport(name = "Unique Product")
        val productsToImport = listOf(conflictingProduct, nonConflictingProduct)

        val existingProduct = createProduct(
            name = "Matching Product",
            shop = commonShop,
            description = commonDescription
        )
        coEvery { productsRepository.getAllProducts() } returns persistentListOf(existingProduct)

        val result = sut.detectConflicts(productsToImport)

        Assert.assertEquals("Should find exactly one conflict", 1, result.size)
        Assert.assertEquals(
            "Should map conflicting product correctly",
            existingProduct,
            result[conflictingProduct]
        )
        Assert.assertTrue(
            "Non-conflicting product should not be in results",
            !result.containsKey(nonConflictingProduct)
        )
    }

    @Test
    fun `detectConflicts matches first existing product when multiple matches exist`() = runTest {
        val shop = getRandomString()
        val description = getRandomString()

        val productToImport = createProductExport(
            name = "Duplicate Product",
            shop = shop,
            description = description
        )
        val existingProduct1 = createProduct(
            id = 1L,
            name = "Duplicate Product",
            shop = shop,
            description = description
        )
        val existingProduct2 = createProduct(
            id = 2L,
            name = "Duplicate Product",
            shop = shop,
            description = description
        )

        coEvery { productsRepository.getAllProducts() } returns persistentListOf(
            existingProduct1,
            existingProduct2
        )

        val result = sut.detectConflicts(listOf(productToImport))

        Assert.assertEquals("Should find exactly one conflict", 1, result.size)
        Assert.assertEquals(
            "Should match with first existing product",
            existingProduct1,
            result[productToImport]
        )
    }

    private fun createProductExport(
        name: String = getRandomString(),
        shop: String = getRandomString(),
        description: String = getRandomString(),
        type: HateRateType = HateRateType.HATE
    ) = ProductExport(
        id = getRandomLong(),
        name = name,
        description = description,
        shop = shop,
        type = type,
        createdDate = OffsetDateTime.now().toString(),
        productFolderName = getRandomString(),
        images = emptyList()
    )

    private fun createProduct(
        id: Long = getRandomLong(),
        name: String = getRandomString(),
        shop: String = getRandomString(),
        description: String = getRandomString(),
        type: HateRateType = HateRateType.HATE
    ) = getFakeProduct().copy(
        id = id,
        name = name,
        description = description,
        shop = shop,
        type = type
    )
}
