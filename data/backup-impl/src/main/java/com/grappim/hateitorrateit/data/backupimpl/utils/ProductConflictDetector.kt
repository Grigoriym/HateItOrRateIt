package com.grappim.hateitorrateit.data.backupimpl.utils

import com.grappim.hateitorrateit.data.backupapi.models.ProductExport
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.Product
import javax.inject.Inject

/**
 * Handles detection of conflicts between products to be imported and existing products
 */
class ProductConflictDetector @Inject constructor(
    private val productsRepository: ProductsRepository
) {

    /**
     * Detects conflicts between products to be imported and existing products
     * @param productsToImport List of products from the backup to be imported
     * @return Map of ProductExport to existing Product for conflicting products
     */
    suspend fun detectConflicts(
        productsToImport: List<ProductExport>
    ): Map<ProductExport, Product> {
        val existingProducts = productsRepository.getAllProducts()
        val conflicts = mutableMapOf<ProductExport, Product>()

        productsToImport.forEach { importProduct ->
            val matchingProduct = findMatchingProduct(importProduct, existingProducts)
            if (matchingProduct != null) {
                conflicts[importProduct] = matchingProduct
            }
        }

        return conflicts
    }

    /**
     * Finds a matching existing product based on name, type, shop, and description
     * Products are considered matching if all these fields are identical
     * @param importProduct Product from backup to check for conflicts
     * @param existingProducts List of existing products in the database
     * @return Matching existing product if found, null otherwise
     */
    private fun findMatchingProduct(
        importProduct: ProductExport,
        existingProducts: List<Product>
    ): Product? = existingProducts.find { existing ->
        existing.name.equals(importProduct.name, ignoreCase = true) &&
            existing.type == importProduct.type &&
            existing.shop.equals(importProduct.shop, ignoreCase = true) &&
            existing.description.equals(importProduct.description, ignoreCase = true)
    }
}
