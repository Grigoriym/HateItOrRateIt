package com.grappim.hateitorrateit.feature.details.ui.navigation

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
data class ProductImageNavDestination(val productId: Long, val index: Int)

fun NavController.navigateToProductImage(productId: Long, index: Int) {
    navigate(route = ProductImageNavDestination(productId, index))
}
